import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { notifications } from '@mantine/notifications';
import {
  login as loginApi,
  register as registerApi,
  logout as logoutApi,
} from '@/lib/auth';
import type { LoginRequest, RegisterRequest } from '@/types/auth';

export function useAuth() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const login = async (credentials: LoginRequest) => {
    setIsLoading(true);
    try {
      await loginApi(credentials);
      notifications.show({
        title: 'Success',
        message: 'You are now logged in!',
        color: 'green',
      });
      router.push('/workspace');
      router.refresh();
    } catch (error) {
      notifications.show({
        title: 'Error',
        message: error instanceof Error ? error.message : 'Login failed',
        color: 'red',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (userData: RegisterRequest) => {
    setIsLoading(true);
    try {
      await registerApi(userData);
      notifications.show({
        title: 'Success',
        message:
          'Thank you for signing up! You can now log in with your credentials.',
        color: 'green',
      });
      router.push('/login');
    } catch (error) {
      notifications.show({
        title: 'Error',
        message: error instanceof Error ? error.message : 'Registration failed',
        color: 'red',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    setIsLoading(true);
    try {
      await logoutApi();
      router.push('/login');
      router.refresh();
    } catch {
      notifications.show({
        title: 'Error',
        message: 'Failed to logout',
        color: 'red',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return { login, register, logout, isLoading };
}
