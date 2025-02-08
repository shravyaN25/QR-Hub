import { NextRequest, NextResponse } from 'next/server';

export async function GET(request: NextRequest) {
  try {
    const searchParams = request.nextUrl.searchParams;
    const queryParams = new URLSearchParams({
      contents: searchParams.get('contents') ?? '',
      size: searchParams.get('size') ?? '',
      correction: searchParams.get('correction') ?? '',
      type: searchParams.get('type') ?? '',
      fcolor: searchParams.get('fcolor') ?? '',
      bcolor: searchParams.get('bcolor') ?? '',
      margin: searchParams.get('margin') ?? '',
    });

    const cookie = request.headers.get('cookie') ?? '';

    const response = await fetch(
      `${process.env.API_URL}/qrcode/generate?${queryParams}`,
      {
        method: 'GET',
        headers: { cookie },
      },
    );

    if (!response.ok) {
      throw new Error('Failed to generate QR code');
    }

    return new NextResponse(response.body, {
      status: response.status,
      headers: {
        'Content-Type':
          response.headers.get('Content-Type') ?? 'application/octet-stream',
      },
    });
  } catch (error) {
    return NextResponse.json(
      { error: `Failed to generate QR code: ${error}` },
      { status: 500 },
    );
  }
}
