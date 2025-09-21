"use client"
import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { login } from '@/lib/auth'
import { useRouter } from 'next/navigation'
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

const LoginSchema = z.object({
  email: z.email({ message: 'Invalid email' }),
  password: z.string().min(6, { message: 'At least 6 characters' }),
  httponly: z.boolean().default(true),
})

type LoginValues = z.input<typeof LoginSchema>

export default function LoginPage() {
  const router = useRouter()
  const [error, setError] = useState('')

  const { register, handleSubmit, formState: { errors, isSubmitting }, watch } = useForm<LoginValues>({
    resolver: zodResolver(LoginSchema),
    defaultValues: { email: 'user@example.com', password: 'password', httponly: true },
    mode: 'onTouched',
  })

  const { mutateAsync } = useMutation({
    mutationFn: (values: LoginValues) => login(values.email, values.password, values.httponly),
  })

  const onSubmit = async (values: LoginValues) => {
    setError('')
    try {
      await mutateAsync(values)
      router.replace('/dashboard')
    } catch {
      setError('Invalid credentials')
    }
  }

  const httponly = watch('httponly')

  return (
    <main className="min-h-screen flex items-center justify-center p-8">
      <form onSubmit={handleSubmit(onSubmit)} className="w-full max-w-sm border p-6 rounded flex flex-col gap-3">
        <h1 className="text-xl font-semibold">Login</h1>
        <label className="text-sm">Email</label>
        <input className="border p-2" placeholder="email" {...register('email')} />
        {errors.email && <span className="text-red-600 text-xs">{errors.email.message}</span>}

        <label className="text-sm">Password</label>
        <input className="border p-2" type="password" placeholder="password" {...register('password')} />
        {errors.password && <span className="text-red-600 text-xs">{errors.password.message}</span>}

        <label className="text-sm flex items-center gap-2">
          <input type="checkbox" {...register('httponly')} />
          Use HttpOnly cookies
        </label>
        <button className="border px-3 py-2" disabled={isSubmitting} type="submit">{isSubmitting ? 'Signing in...' : `Login (${httponly ? 'HttpOnly' : 'JSON tokens'})`}</button>

        {error && <div className="text-red-600 text-sm">{error}</div>}
        <a className="text-blue-600 text-sm" href="/signup">Create account</a>
      </form>
    </main>
  )
}


