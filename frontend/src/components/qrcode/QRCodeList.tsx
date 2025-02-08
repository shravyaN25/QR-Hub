import {
  Grid,
  Text,
  Button,
  Card,
  Group,
  Badge,
  ActionIcon,
  Stack,
  Modal,
  TextInput,
  Paper,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import {
  IconPencil,
  IconDownload,
  IconTrash,
  IconPlus,
} from '@tabler/icons-react';
import { useState } from 'react';
import { notifications } from '@mantine/notifications';
import Image from 'next/image';
import { EditQRCodeForm } from '@/components/qrcode/EditQRCodeForm';
import type { QRCodeResponse, EditQRCodeFormValues } from '@/types/qrcode';

interface QRCodeListProps {
  qrCodes: QRCodeResponse[];
  isLoading: boolean;
  onDelete: (id: string) => Promise<void>;
  onUpdate: (id: string, values: EditQRCodeFormValues) => Promise<void>;
  onTabChange: (value: string) => void;
  refetch: () => Promise<void>;
}

export function QRCodeList({
  qrCodes,
  isLoading,
  onDelete,
  onUpdate,
  onTabChange,
  refetch,
}: Readonly<QRCodeListProps>) {
  const [isGenerating, setIsGenerating] = useState(false);
  const [imageUrl, setImageUrl] = useState<string>('');
  const [selectedQRCode, setSelectedQRCode] = useState<QRCodeResponse | null>(
    null,
  );
  const [deleteConfirmation, setDeleteConfirmation] = useState<string>('');
  const [error, setError] = useState<string>();

  const [deleteOpened, { open: openDelete, close: closeDelete }] =
    useDisclosure(false);
  const [editOpened, { open: openEdit, close: closeEdit }] =
    useDisclosure(false);

  const handleDownload = async (qrCode: QRCodeResponse) => {
    try {
      const queryParams = new URLSearchParams({
        contents: qrCode.content,
        size: qrCode.size.toString(),
        correction: qrCode.errorCorrection,
        type: qrCode.format,
        fcolor: encodeURIComponent(qrCode.foregroundColor),
        bcolor: encodeURIComponent(qrCode.backgroundColor),
        margin: qrCode.margin.toString(),
      });

      const response = await fetch(`/api/qrcode/generate?${queryParams}`);

      if (!response.ok) {
        throw new Error('Failed to generate QR code');
      }

      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${qrCode.name}-${Date.now()}.${qrCode.format}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      notifications.show({
        title: 'Success',
        message: 'QR code downloaded successfully!',
        color: 'green',
      });
    } catch (error) {
      notifications.show({
        title: 'Error',
        message:
          error instanceof Error ? error.message : 'Failed to download QR code',
        color: 'red',
      });
    }
  };

  const handlePreviewEdit = async (values: EditQRCodeFormValues) => {
    try {
      setIsGenerating(true);
      setError(undefined);

      const fcolorEncoded = encodeURIComponent(values.fcolor);
      const bcolorEncoded = encodeURIComponent(values.bcolor);

      const queryParams = new URLSearchParams({
        contents: values.content,
        size: values.size.toString(),
        correction: values.correction,
        type: values.format,
        fcolor: fcolorEncoded,
        bcolor: bcolorEncoded,
        margin: values.margin.toString(),
      });

      const response = await fetch(`/api/qrcode/generate?${queryParams}`);

      if (!response.ok) {
        throw new Error('Failed to generate QR code');
      }

      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      setImageUrl(url);
    } catch (error) {
      setError(
        error instanceof Error ? error.message : 'Failed to generate QR code',
      );
    } finally {
      setIsGenerating(false);
    }
  };

  const handleEdit = async (values: EditQRCodeFormValues) => {
    if (!selectedQRCode) return;

    try {
      await onUpdate(selectedQRCode.id, values);
      closeEdit();
      await refetch();
    } catch (error) {
      notifications.show({
        title: 'Error',
        message:
          error instanceof Error ? error.message : 'Failed to update QR code',
        color: 'red',
      });
    }
  };

  const handleDelete = async () => {
    if (!selectedQRCode) return;

    try {
      await onDelete(selectedQRCode.id);
      closeDelete();
      setDeleteConfirmation('');
      await refetch();
    } catch (error) {
      notifications.show({
        title: 'Error',
        message:
          error instanceof Error ? error.message : 'Failed to delete QR code',
        color: 'red',
      });
    }
  };

  if (isLoading) {
    return (
      <Text size="sm" c="dimmed" ta="center">
        Loading...
      </Text>
    );
  }

  if (qrCodes.length === 0) {
    return (
      <Stack align="center" py="xl">
        <Text size="xl" fw={700} c="dimmed">
          No QR codes yet
        </Text>
        <Button
          leftSection={<IconPlus size={14} />}
          variant="light"
          onClick={() => onTabChange('generator')}
        >
          Create
        </Button>
      </Stack>
    );
  }

  return (
    <>
      <Grid>
        {qrCodes.map((qrCode) => (
          <Grid.Col key={qrCode.id} span={{ base: 12, sm: 6, md: 4 }}>
            <Card shadow="sm" padding="lg" radius="md" withBorder>
              <Card.Section withBorder inheritPadding py="xs">
                <Group justify="space-between">
                  <Text fw={500} truncate>
                    {qrCode.name}
                  </Text>
                  <Badge variant="light">{qrCode.format}</Badge>
                </Group>
              </Card.Section>
              <Text size="sm" c="dimmed" mt="md" lineClamp={2}>
                {qrCode.description ?? 'No description provided'}
              </Text>
              <Text size="sm" c="dimmed" mt="sm">
                Created: {new Date(qrCode.createdAt).toLocaleDateString()}
              </Text>
              <Group mt="md" justify="flex-end">
                <ActionIcon
                  variant="light"
                  color="blue"
                  onClick={() => handleDownload(qrCode)}
                  aria-label="Download"
                >
                  <IconDownload style={{ width: '70%', height: '70%' }} />
                </ActionIcon>
                <ActionIcon
                  variant="light"
                  color="yellow"
                  onClick={() => {
                    setSelectedQRCode(qrCode);
                    handlePreviewEdit({
                      ...qrCode,
                      description: qrCode.description ?? '',
                      fcolor: qrCode.foregroundColor,
                      bcolor: qrCode.backgroundColor,
                      correction: qrCode.errorCorrection,
                    });
                    openEdit();
                  }}
                  aria-label="Edit"
                >
                  <IconPencil style={{ width: '70%', height: '70%' }} />
                </ActionIcon>
                <ActionIcon
                  variant="light"
                  color="red"
                  onClick={() => {
                    setSelectedQRCode(qrCode);
                    setDeleteConfirmation('');
                    openDelete();
                  }}
                  aria-label="Delete"
                >
                  <IconTrash style={{ width: '70%', height: '70%' }} />
                </ActionIcon>
              </Group>
            </Card>
          </Grid.Col>
        ))}
      </Grid>
      <Modal
        opened={deleteOpened}
        onClose={closeDelete}
        title="Delete QR Code"
        centered
        overlayProps={{
          backgroundOpacity: 0.55,
          blur: 3,
        }}
      >
        <Text size="sm">
          To delete, please type <strong>{selectedQRCode?.name}</strong> in the
          input below. Note that this action cannot be undone.
        </Text>
        <TextInput
          mt="md"
          placeholder={selectedQRCode?.name ?? ''}
          value={deleteConfirmation}
          onChange={(e) => setDeleteConfirmation(e.currentTarget.value)}
        />
        <Group justify="flex-end" mt="xl">
          <Button variant="default" onClick={closeDelete}>
            Cancel
          </Button>
          <Button
            color="red"
            onClick={handleDelete}
            disabled={deleteConfirmation !== selectedQRCode?.name}
          >
            Delete
          </Button>
        </Group>
      </Modal>
      <Modal
        opened={editOpened}
        onClose={closeEdit}
        title="Edit QR Code"
        size="xl"
        centered
      >
        {selectedQRCode && (
          <Grid>
            <Grid.Col span={{ base: 12, md: 6 }}>
              <EditQRCodeForm
                qrCode={selectedQRCode}
                onSubmit={handleEdit}
                onPreview={handlePreviewEdit}
                isLoading={isGenerating}
                error={error}
              />
            </Grid.Col>
            <Grid.Col span={{ base: 12, md: 6 }}>
              <Paper shadow="xs" p="md">
                <Stack>
                  <Text fw={500} size="lg" ta="center">
                    Preview
                  </Text>
                  {imageUrl && (
                    <div
                      style={{
                        position: 'relative',
                        width: '100%',
                        height: '300px',
                      }}
                    >
                      <Image
                        src={imageUrl}
                        alt="QR Code Preview"
                        fill
                        style={{ objectFit: 'contain' }}
                      />
                    </div>
                  )}
                </Stack>
              </Paper>
            </Grid.Col>
          </Grid>
        )}
      </Modal>
    </>
  );
}
