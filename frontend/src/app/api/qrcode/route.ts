import { NextRequest, NextResponse } from 'next/server';
import type { SaveQRCodeRequest } from '@/types/qrcode';

export async function GET(request: NextRequest) {
  try {
    const cookie = request.headers.get('cookie') ?? '';
    const response = await fetch(`${process.env.API_URL}/qrcode`, {
      headers: { cookie },
    });

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        { error: data.error || 'Failed to fetch QR codes' },
        { status: response.status },
      );
    }

    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: `Failed to fetch QR codes: ${error}` },
      { status: 500 },
    );
  }
}

export async function POST(request: NextRequest) {
  try {
    const body: SaveQRCodeRequest = await request.json();
    const cookie = request.headers.get('cookie') ?? '';

    const response = await fetch(`${process.env.API_URL}/qrcode`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        cookie,
      },
      body: JSON.stringify(body),
    });

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        { error: data.error || 'Failed to save QR code' },
        { status: response.status },
      );
    }

    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: `Failed to save QR code: ${error}` },
      { status: 500 },
    );
  }
}
