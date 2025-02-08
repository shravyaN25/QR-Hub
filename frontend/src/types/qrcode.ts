export interface QRCodeResponse {
  id: string;
  content: string;
  name: string;
  description?: string;
  size: number;
  format: string;
  foregroundColor: string;
  backgroundColor: string;
  margin: number;
  errorCorrection: string;
  createdAt: string;
  updatedAt: string;
}

export interface GenerateQRCodeResponse {
  url: string;
}

export interface SaveQRCodeRequest {
  content: string;
  name: string;
  description?: string;
  size: number;
  format: string;
  foregroundColor: string;
  backgroundColor: string;
  margin: number;
  errorCorrection: string;
}

export interface CreateQRCodeFormValues {
  content: string;
  size: number;
  correction: string;
  format: string;
  fcolor: string;
  bcolor: string;
  margin: number;
}

export interface SaveFormValues {
  name: string;
  description: string;
}

export type EditQRCodeFormValues = CreateQRCodeFormValues & SaveFormValues;
