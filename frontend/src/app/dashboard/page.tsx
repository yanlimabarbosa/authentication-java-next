"use client"
import Guard from '@/components/Guard'
import { useSession } from '@/hooks/useSession'

export default function Dashboard() {
  const { data } = useSession()
  return (
    <Guard>
      <main className="p-8">
        <h1 className="text-2xl font-semibold mb-4">Dashboard</h1>
        <p className="mb-4">Authenticated. Below are your permissions:</p>
        <pre className="bg-gray-100 p-3 text-xs overflow-auto max-h-96">{JSON.stringify(data, null, 2)}</pre>
      </main>
    </Guard>
  )
}


