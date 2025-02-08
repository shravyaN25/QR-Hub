'use client';

import {
  TextInput,
  Button,
  Select,
  NumberInput,
  ColorInput,
  Paper,
  Stack,
  Group,
  Tooltip,
  ActionIcon,
  Text,
} from '@mantine/core';
import { IconInfoCircle } from '@tabler/icons-react';
import { useForm } from '@mantine/form';
import type { CreateQRCodeFormValues } from '@/types/qrcode';

const ERROR_CORRECTION_INFO =
  'Error correction capability allows a QR code to be readable even when partially damaged or obscured. Higher levels provide more redundancy but increase QR code size.';

const CORRECTION_LEVELS = [
  { value: 'L', label: 'Low (7%)' },
  { value: 'M', label: 'Medium (15%)' },
  { value: 'Q', label: 'High (25%)' },
  { value: 'H', label: 'Ultra High (30%)' },
];

const IMAGE_FORMATS = [
  { value: 'png', label: 'PNG' },
  { value: 'jpeg', label: 'JPEG' },
  { value: 'gif', label: 'GIF' },
];

interface CreateQRCodeFormProps {
  onGenerate: (values: CreateQRCodeFormValues) => void;
  isLoading?: boolean;
  error?: string;
  initialValues?: CreateQRCodeFormValues;
}

export function CreateQRCodeForm({
  onGenerate,
  isLoading = false,
  error,
  initialValues,
}: Readonly<CreateQRCodeFormProps>) {
  const form = useForm<CreateQRCodeFormValues>({
    initialValues: initialValues ?? {
      content: '',
      size: 250,
      correction: 'M',
      format: 'png',
      fcolor: '#000000',
      bcolor: '#FFFFFF',
      margin: 4,
    },
    validate: {
      content: (value) => (!value ? 'Content is required' : null),
      size: (value) =>
        value < 150 || value > 350
          ? 'Size must be between 150 and 350 pixels'
          : null,
      margin: (value) =>
        value < 0 || value > 50
          ? 'Margin must be between 0 and 50 pixels'
          : null,
    },
  });

  return (
    <Paper shadow="xs" p="md">
      {error && (
        <Text c="red" size="sm" mb="md">
          {error}
        </Text>
      )}
      <form onSubmit={form.onSubmit(onGenerate)}>
        <Stack>
          <TextInput
            label="Content"
            placeholder="Enter content (URL, text etc.)"
            required
            {...form.getInputProps('content')}
          />

          <Group grow>
            <NumberInput
              label="Size (pixels)"
              placeholder="250"
              min={150}
              max={350}
              {...form.getInputProps('size')}
            />

            <NumberInput
              label="Margin (pixels)"
              placeholder="4"
              min={0}
              max={50}
              {...form.getInputProps('margin')}
            />
          </Group>

          <Group grow align="flex-start">
            <Select
              label={
                <Group gap="xs">
                  <span>Error Correction</span>
                  <Tooltip label={ERROR_CORRECTION_INFO} multiline w={220}>
                    <ActionIcon
                      variant="subtle"
                      size="sm"
                      radius="xl"
                      aria-label="Info about error correction"
                    >
                      <IconInfoCircle style={{ width: '70%', height: '70%' }} />
                    </ActionIcon>
                  </Tooltip>
                </Group>
              }
              data={CORRECTION_LEVELS}
              {...form.getInputProps('correction')}
            />

            <Select
              label="Image Format"
              data={IMAGE_FORMATS}
              {...form.getInputProps('format')}
            />
          </Group>

          <Group grow>
            <ColorInput
              label="Foreground Color"
              {...form.getInputProps('fcolor')}
            />

            <ColorInput
              label="Background Color"
              {...form.getInputProps('bcolor')}
            />
          </Group>

          <Button type="submit" loading={isLoading}>
            Preview
          </Button>
        </Stack>
      </form>
    </Paper>
  );
}
