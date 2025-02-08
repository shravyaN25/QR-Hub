import { Outfit } from 'next/font/google';
import type { Metadata } from 'next';
import { cookies } from 'next/headers';
import {
  ColorSchemeScript,
  MantineProvider,
  mantineHtmlProps,
} from '@mantine/core';
import { Notifications } from '@mantine/notifications';
import '@mantine/core/styles.css';
import '@mantine/notifications/styles.css';
import { theme } from '@/theme';
import { Navbar } from '@/components/layout/Navbar';
import { Footer } from '@/components/layout/Footer';

const outfit = Outfit({ subsets: ['latin'] });

export const metadata: Metadata = {
  title: 'QR Code Generator',
  description: 'Generate and manage your QR codes',
};

export default async function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  const isAuthenticated = Boolean(token);
  return (
    <html lang="en" {...mantineHtmlProps} className={outfit.className}>
      <head>
        <ColorSchemeScript />
      </head>
      <body>
        <MantineProvider theme={theme}>
          <Notifications
            position="top-right"
            containerWidth={320}
            autoClose={4000}
          />
          <Navbar isAuthenticated={isAuthenticated} />
          {children}
          <Footer />
        </MantineProvider>
      </body>
    </html>
  );
}
