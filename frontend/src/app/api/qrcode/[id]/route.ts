import { NextRequest, NextResponse } from 'next/server';
import type { SaveQRCodeRequest } from '@/types/qrcode';

export async function PUT(request: NextRequest) {
  try {
    const pathSegments = request.nextUrl.pathname.split('/');
    const id = pathSegments[pathSegments.length - 1];
    const body: SaveQRCodeRequest = await request.json();
    const cookie = request.headers.get('cookie') ?? '';

    const response = await fetch(`${process.env.API_URL}/qrcode/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        cookie,
      },
      body: JSON.stringify(body),
    });

    const data = await response.json();

    if (!response.ok) {
      return NextResponse.json(
        { error: data.error || 'Failed to update QR code' },
        { status: response.status },
      );
    }

    return NextResponse.json(data);
  } catch (error) {
    return NextResponse.json(
      { error: `Failed to update QR code: ${error}` },
      { status: 500 },
    );
  }
}

export async function DELETE(request: NextRequest) {
  try {
    const pathSegments = request.nextUrl.pathname.split('/');
    const id = pathSegments[pathSegments.length - 1];
    const cookie = request.headers.get('cookie') ?? '';

    const response = await fetch(`${process.env.API_URL}/qrcode/${id}`, {
      method: 'DELETE',
      headers: { cookie },
    });

    if (!response.ok) {
      const data = await response.json();
      return NextResponse.json(
        { error: data.error || 'Failed to delete QR code' },
        { status: response.status },
      );
    }

    return new NextResponse(null, { status: 204 });
  } catch (error) {
    return NextResponse.json(
      { error: `Failed to delete QR code: ${error}` },
      { status: 500 },
    );
  }
}
