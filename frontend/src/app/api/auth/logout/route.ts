import { NextResponse } from 'next/server';

export async function POST() {
  try {
    const response = await fetch(`${process.env.API_URL}/auth/logout`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: 'Logout failed' },
        { status: response.status },
      );
    }

    const res = NextResponse.json({ message: 'Logged out successfully' });
    const setCookie = response.headers.get('set-cookie');
    if (setCookie) {
      res.headers.set('set-cookie', setCookie);
    }

    return res;
  } catch (error) {
    return NextResponse.json(
      { error: `Failed to logout: ${error}` },
      { status: 500 },
    );
  }
}
