'use client';

import { Container, Title, Paper, Tabs } from '@mantine/core';
import { useState, useEffect } from 'react';
import { notifications } from '@mantine/notifications';
import { QRCodeList } from '@/components/qrcode/QRCodeList';
import { QRCodeGenerator } from '@/components/qrcode/QRCodeGenerator';
import type { QRCodeResponse, EditQRCodeFormValues } from '@/types/qrcode';
import classes from './page.module.css';

export default function WorkspacePage() {
  const [activeTab, setActiveTab] = useState<string | null>('list');
  const [qrCodes, setQrCodes] = useState<QRCodeResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchQRCodes = async () => {
    try {
      setIsLoading(true);
      const response = await fetch('/api/qrcode');
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Failed to fetch QR codes');
      }

      setQrCodes(data);
    } catch (error) {
      notifications.show({
        title: 'Error',
        message:
          error instanceof Error ? error.message : 'Failed to fetch QR codes',
        color: 'red',
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchQRCodes();
  }, []);

  const handleDelete = async (id: string) => {
    const response = await fetch(`/api/qrcode/${id}`, {
      method: 'DELETE',
    });

    if (!response.ok) {
      const data = await response.json();
      throw new Error(data.error || 'Failed to delete QR code');
    }

    notifications.show({
      title: 'Success',
      message: 'QR code deleted successfully!',
      color: 'green',
    });
  };

  const handleUpdate = async (id: string, values: EditQRCodeFormValues) => {
    const response = await fetch(`/api/qrcode/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        content: values.content,
        name: values.name,
        description: values.description,
        size: values.size,
        format: values.format,
        foregroundColor: values.fcolor,
        backgroundColor: values.bcolor,
        margin: values.margin,
        errorCorrection: values.correction,
      }),
    });

    const data = await response.json();

    if (!response.ok) {
      throw new Error(data.error || 'Failed to update QR code');
    }

    notifications.show({
      title: 'Success',
      message: 'QR code updated successfully!',
      color: 'green',
    });
  };

  return (
    <div className={classes.wrapper}>
      <Container size="lg">
        <Paper className={classes.paper} withBorder>
          <Title className={classes.title} order={2}>
            Workspace
          </Title>

          <Tabs
            value={activeTab}
            onChange={setActiveTab}
            className={classes.tabs}
          >
            <Tabs.List>
              <Tabs.Tab value="list">My QR Codes</Tabs.Tab>
              <Tabs.Tab value="generator">Generator</Tabs.Tab>
            </Tabs.List>

            <Tabs.Panel value="list" pt="xl">
              <QRCodeList
                qrCodes={qrCodes}
                isLoading={isLoading}
                onDelete={handleDelete}
                onUpdate={handleUpdate}
                onTabChange={setActiveTab}
                refetch={fetchQRCodes}
              />
            </Tabs.Panel>

            <Tabs.Panel value="generator" pt="xl">
              <QRCodeGenerator
                onTabChange={setActiveTab}
                onSaved={fetchQRCodes}
              />
            </Tabs.Panel>
          </Tabs>
        </Paper>
      </Container>
    </div>
  );
}
