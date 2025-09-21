import type { Metadata } from "next"
import "./globals.css"
import Providers from '@/components/Providers'

export const metadata: Metadata = {
  title: "Auth App",
  description: "Auth + RBAC Demo",
}

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <Providers>
          {children}
        </Providers>
      </body>
    </html>
  )
}
