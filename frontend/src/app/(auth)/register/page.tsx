'use client';

import {
  TextInput,
  PasswordInput,
  Paper,
  Title,
  Container,
  Button,
  Text,
} from '@mantine/core';
import { useForm } from '@mantine/form';
import Link from 'next/link';
import { useAuth } from '@/hooks/useAuth';
import classes from './page.module.css';

export default function RegisterPage() {
  const { register, isLoading } = useAuth();

  const form = useForm({
    initialValues: {
      email: '',
      name: '',
      password: '',
    },
    validate: {
      email: (value) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
      password: (value) =>
        value.length < 6 ? 'Password must be at least 6 characters' : null,
      name: (value) =>
        value.length < 2 ? 'Name must be at least 2 characters' : null,
    },
  });

  return (
    <Container size={420} my={40}>
      <Title ta="center" className={classes.title}>
        Create your account
      </Title>

      <Text c="dimmed" size="sm" ta="center" mt={5}>
        Already have an account?{' '}
        <Text component={Link} href="/login" size="sm" c="cyan">
          Log in
        </Text>
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <form onSubmit={form.onSubmit((values) => register(values))}>
          <TextInput
            label="Name"
            placeholder="Your name"
            required
            {...form.getInputProps('name')}
          />
          <TextInput
            label="Email"
            placeholder="you@example.com"
            required
            mt="md"
            {...form.getInputProps('email')}
          />
          <PasswordInput
            label="Password"
            placeholder="Your password"
            required
            mt="md"
            {...form.getInputProps('password')}
          />
          <Button type="submit" fullWidth mt="xl" loading={isLoading}>
            Create
          </Button>
        </form>
      </Paper>
    </Container>
  );
}
