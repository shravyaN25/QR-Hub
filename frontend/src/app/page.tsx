import Link from 'next/link';
import { Container, Title, Text, Button, Group } from '@mantine/core';
import classes from './page.module.css';

export default function HomePage() {
  return (
    <Container size="lg" py="xl">
      <div className={classes.inner}>
        <div className={classes.content}>
          <Title className={classes.title}>
            Create and manage your{' '}
            <Text
              component="span"
              inherit
              variant="gradient"
              gradient={{ from: 'cyan', to: 'indigo' }}
            >
              QR Codes
            </Text>
          </Title>

          <Text className={classes.description} mt={30}>
            Generate customized QR Codes for your needs! Save, manage and access
            them anytime.
          </Text>

          <Group mt={40}>
            <Button
              size="lg"
              variant="gradient"
              gradient={{ from: 'cyan', to: 'indigo' }}
              component={Link}
              href="/register"
            >
              Get Started
            </Button>
          </Group>
        </div>
      </div>
    </Container>
  );
}
