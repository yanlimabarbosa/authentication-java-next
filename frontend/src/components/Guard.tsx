"use client"
import { ReactNode, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useSession } from '@/hooks/useSession'

export default function Guard({ children }: { children: ReactNode }) {
  const { isAuthenticated, isLoading } = useSession()
  const router = useRouter()

  useEffect(() => {
    if (!isLoading && !isAuthenticated) router.replace('/login')
  }, [isLoading, isAuthenticated, router])

  if (isLoading) return null
  if (!isAuthenticated) return null
  return <>{children}</>
}


