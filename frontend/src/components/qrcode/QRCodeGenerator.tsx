import { Grid } from '@mantine/core';
import { useState } from 'react';
import { CreateQRCodeForm } from '@/components/qrcode/CreateQRCodeForm';
import { QRCodePreview } from '@/components/qrcode/QRCodePreview';
import type { CreateQRCodeFormValues } from '@/types/qrcode';

interface QRCodeGeneratorProps {
  onTabChange: (value: string) => void;
  onSaved: () => Promise<void>;
}

export function QRCodeGenerator({
  onTabChange,
  onSaved,
}: Readonly<QRCodeGeneratorProps>) {
  const [isGenerating, setIsGenerating] = useState(false);
  const [imageUrl, setImageUrl] = useState<string>('');
  const [formValues, setFormValues] = useState<CreateQRCodeFormValues>();
  const [error, setError] = useState<string>();

  const handleGenerate = async (values: CreateQRCodeFormValues) => {
    try {
      setIsGenerating(true);
      setError(undefined);
      setFormValues(values);

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

  return (
    <Grid>
      <Grid.Col span={{ base: 12, md: 6 }}>
        <CreateQRCodeForm
          onGenerate={handleGenerate}
          isLoading={isGenerating}
          error={error}
        />
      </Grid.Col>
      <Grid.Col span={{ base: 12, md: 6 }}>
        <QRCodePreview
          imageUrl={imageUrl}
          formValues={formValues}
          onSaved={onSaved}
          onTabChange={onTabChange}
        />
      </Grid.Col>
    </Grid>
  );
}
