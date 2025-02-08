'use client';

import {
  Container,
  Group,
  Button,
  Text,
  Menu,
  UnstyledButton,
  Avatar,
  Drawer,
  Stack,
  Burger,
} from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import { IconLayoutDashboard, IconLogout } from '@tabler/icons-react';
import Link from 'next/link';
import { useAuth } from '@/hooks/useAuth';
import classes from './Navbar.module.css';

interface NavbarProps {
  isAuthenticated: boolean;
}

export function Navbar({ isAuthenticated }: Readonly<NavbarProps>) {
  const { logout } = useAuth();
  const [opened, { toggle, close }] = useDisclosure(false);

  const items = isAuthenticated ? (
    <Stack gap="xs">
      <Menu>
        <Menu.Target>
          <UnstyledButton>
            <Avatar size="sm" radius="xl" />
          </UnstyledButton>
        </Menu.Target>
        <Menu.Dropdown>
          <Menu.Item
            component={Link}
            href="/workspace"
            leftSection={<IconLayoutDashboard size={14} />}
          >
            Workspace
          </Menu.Item>
          <Menu.Divider />
          <Menu.Item
            color="red"
            leftSection={<IconLogout size={14} />}
            onClick={logout}
          >
            Logout
          </Menu.Item>
        </Menu.Dropdown>
      </Menu>
    </Stack>
  ) : (
    <Stack gap="xs">
      <Button component={Link} href="/login" variant="default" onClick={close}>
        Log in
      </Button>
      <Button
        component={Link}
        href="/register"
        variant="gradient"
        gradient={{ from: 'cyan', to: 'indigo' }}
        onClick={close}
      >
        Sign up
      </Button>
    </Stack>
  );

  return (
    <header className={classes.header}>
      <Container size="lg" className={classes.inner}>
        <Text
          fw={700}
          variant="gradient"
          gradient={{ from: 'cyan', to: 'indigo' }}
          component={Link}
          href="/"
        >
          QRCodeGenerator
        </Text>

        <Group gap="xs" className={classes.links}>
          {isAuthenticated ? (
            <Menu position="bottom-end" width={200}>
              <Menu.Target>
                <UnstyledButton>
                  <Avatar size="sm" radius="xl" />
                </UnstyledButton>
              </Menu.Target>
              <Menu.Dropdown>
                <Menu.Item
                  component={Link}
                  href="/workspace"
                  leftSection={<IconLayoutDashboard size={14} />}
                >
                  Workspace
                </Menu.Item>
                <Menu.Divider />
                <Menu.Item
                  color="red"
                  leftSection={<IconLogout size={14} />}
                  onClick={logout}
                >
                  Logout
                </Menu.Item>
              </Menu.Dropdown>
            </Menu>
          ) : (
            <>
              <Button component={Link} href="/login" variant="default">
                Log in
              </Button>
              <Button
                component={Link}
                href="/register"
                variant="gradient"
                gradient={{ from: 'cyan', to: 'indigo' }}
              >
                Sign up
              </Button>
            </>
          )}
        </Group>

        <Burger opened={opened} onClick={toggle} className={classes.burger} />

        <Drawer
          opened={opened}
          onClose={close}
          position="right"
          size="xs"
          title=""
        >
          {items}
        </Drawer>
      </Container>
    </header>
  );
}
