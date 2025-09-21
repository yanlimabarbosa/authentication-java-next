import { useQuery } from '@tanstack/react-query'
import { getSelfPermissions } from '@/lib/auth'

export function useSession() {
  const query = useQuery({
    queryKey: ['SELF_PERMISSIONS'],
    queryFn: getSelfPermissions,
    retry: false,
  })

  const isAuthenticated = !!query.data && !query.isError
  return { ...query, isAuthenticated }
}

export function usePermission(resource: string, action: 'acessar'|'criar'|'editar'|'deletar'|'exportar'='acessar') {
  const { data } = useSession()
  const flag = data?.[resource]?.[action]
  return flag === true || flag === 1
}


