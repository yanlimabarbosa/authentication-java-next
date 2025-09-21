"use client"
import Guard from '@/components/Guard'
import { useSession } from '@/hooks/useSession'

export default function PermissionsPanel() {
  const { data } = useSession()
  const rows = Object.entries(data || {})

  return (
    <Guard>
      <main className="p-8">
        <h1 className="text-2xl font-semibold mb-4">Permissions Panel (read-only)</h1>
        <div className="overflow-auto">
          <table className="min-w-[600px] border text-sm">
            <thead>
              <tr>
                <th className="border px-2 py-1 text-left">Resource</th>
                {['acessar','criar','editar','deletar','exportar'].map((a) => (
                  <th key={a} className="border px-2 py-1 text-left">{a}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {rows.map(([resource, perms]) => (
                <tr key={resource}>
                  <td className="border px-2 py-1">{resource}</td>
                  {['acessar','criar','editar','deletar','exportar'].map((a) => (
                    <td key={a} className="border px-2 py-1">{perms?.[a as keyof typeof perms] ? '✔' : ''}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </Guard>
  )
}


