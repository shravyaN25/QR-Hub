'use client';

import { useState } from 'react';
import {
  Paper,
  Text,
  Button,
  Stack,
  Group,
  Modal,
  TextInput,
  Textarea,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import { notifications } from '@mantine/notifications';
import Image from 'next/image';
import type {
  CreateQRCodeFormValues,
  SaveFormValues,
  QRCodeResponse,
} from '@/types/qrcode';

interface QRCodePreviewProps {
  imageUrl: string;
  formValues?: CreateQRCodeFormValues;
  qrCodeToUpdate?: QRCodeResponse;
  onSaved?: () => Promise<void>;
  onTabChange?: (value: string) => void;
}

export function QRCodePreview({
  imageUrl,
  formValues,
  qrCodeToUpdate,
  onSaved,
  onTabChange,
}: Readonly<QRCodePreviewProps>) {
  const [isDownloading, setIsDownloading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [saveModalOpened, setSaveModalOpened] = useState(false);

  const modalTitle = qrCodeToUpdate ? 'Update QR Code' : 'Save QR Code';
  const actionVerb = qrCodeToUpdate ? 'update' : 'save';

  const form = useForm<SaveFormValues>({
    initialValues: {
      name: qrCodeToUpdate?.name ?? '',
      description: qrCodeToUpdate?.description ?? '',
    },
    validate: {
      name: (value) => {
        if (!value) {
          return 'Name is required';
        }
        if (value.length < 3) {
          return 'Name must be at least 3 characters';
        }
        if (value.length > 50) {
          return 'Name must be at most 50 characters';
        }
        return null;
      },
      description: (value) => {
        if (value && value.length > 200) {
          return 'Description must be at most 200 characters';
        }
        return null;
      },
    },
  });

  const handleDownload = async () => {
    try {
      setIsDownloading(true);
      const response = await fetch(imageUrl);
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `qrcode-${Date.now()}.${formValues?.format ?? 'png'}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      notifications.show({
        title: 'Success',
        message: 'QR code downloaded successfully',
        color: 'green',
      });
    } catch (error) {
      notifications.show({
        title: 'Error',
        message:
          error instanceof Error ? error.message : 'Failed to download QR code',
        color: 'red',
      });
    } finally {
      setIsDownloading(false);
    }
  };

  const handleSave = async (values: SaveFormValues) => {
    if (!formValues) return;

    try {
      setIsSaving(true);

      const payload = {
        content: formValues.content,
        name: values.name,
        description: values.description,
        size: formValues.size,
        format: formValues.format,
        foregroundColor: formValues.fcolor,
        backgroundColor: formValues.bcolor,
        margin: formValues.margin,
        errorCorrection: formValues.correction,
      };

      const url = qrCodeToUpdate
        ? `/api/qrcode/${qrCodeToUpdate.id}`
        : '/api/qrcode';

      const method = qrCodeToUpdate ? 'PUT' : 'POST';

      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.error || `Failed to ${actionVerb} QR code`);
      }

      notifications.show({
        title: 'Success',
        message: `QR code ${qrCodeToUpdate ? 'updated' : 'saved'} successfully!`,
        color: 'green',
      });

      setSaveModalOpened(false);
      form.reset();

      if (onSaved) {
        await onSaved();
      }
      if (onTabChange && !qrCodeToUpdate) {
        onTabChange('list');
      }
    } catch (error) {
      notifications.show({
        title: 'Error',
        message:
          error instanceof Error
            ? error.message
            : `Failed to ${actionVerb} QR code`,
        color: 'red',
      });
    } finally {
      setIsSaving(false);
    }
  };

  if (!imageUrl) {
    return null;
  }

  return (
    <>
      <Paper shadow="xs" p="md">
        <Stack>
          <Text fw={500} size="lg" ta="center">
            Generated QR Code
          </Text>
          <div style={{ position: 'relative', width: '100%', height: '300px' }}>
            <Image
              src={imageUrl}
              alt="Generated QR Code"
              fill
              style={{ objectFit: 'contain' }}
            />
          </div>
          <Group grow>
            <Button onClick={handleDownload} loading={isDownloading}>
              Download
            </Button>
            {formValues && (
              <Button
                variant="light"
                onClick={() => setSaveModalOpened(true)}
                loading={isSaving}
              >
                {qrCodeToUpdate ? 'Update' : 'Save'}
              </Button>
            )}
          </Group>
        </Stack>
      </Paper>

      <Modal
        opened={saveModalOpened}
        onClose={() => setSaveModalOpened(false)}
        title={modalTitle}
      >
        <form onSubmit={form.onSubmit(handleSave)}>
          <TextInput
            label="Name"
            placeholder="My QR Code"
            required
            mb="md"
            {...form.getInputProps('name')}
          />
          <Textarea
            label="Description"
            placeholder="Optional description"
            mb="xl"
            {...form.getInputProps('description')}
          />
          <Group justify="flex-end">
            <Button variant="default" onClick={() => setSaveModalOpened(false)}>
              Cancel
            </Button>
            <Button type="submit" loading={isSaving}>
              {qrCodeToUpdate ? 'Update' : 'Save'}
            </Button>
          </Group>
        </form>
      </Modal>
    </>
  );
}
