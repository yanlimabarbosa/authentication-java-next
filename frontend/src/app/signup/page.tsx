"use client"
import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { signup } from '@/lib/auth'
import { useRouter } from 'next/navigation'
import { useState } from 'react'

const SignupSchema = z.object({
  email: z.email({ message: 'Invalid email' }),
  name: z.string().min(2, { message: 'At least 2 characters' }),
  password: z.string().min(6, { message: 'At least 6 characters' }),
})

type SignupValues = z.infer<typeof SignupSchema>

export default function SignupPage() {
  const router = useRouter()
  const [error, setError] = useState('')
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<SignupValues>({
    resolver: zodResolver(SignupSchema),
    mode: 'onTouched',
  })

  const onSubmit = async (values: SignupValues) => {
    try {
      setError('')
      await signup(values.email, values.name, values.password)
      alert('Account created successfully! You can now login.')
      router.push('/login')
    } catch (err: any) {
      setError(err.response?.data || 'Failed to create account')
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center p-8">
      <form onSubmit={handleSubmit(onSubmit)} className="w-full max-w-sm border p-6 rounded flex flex-col gap-3">
        <h1 className="text-xl font-semibold">Create account</h1>
        {error && <div className="text-red-600 text-sm">{error}</div>}
        <label className="text-sm">Name</label>
        <input className="border p-2" placeholder="your name" {...register('name')} />
        {errors.name && <span className="text-red-600 text-xs">{errors.name.message}</span>}

        <label className="text-sm">Email</label>
        <input className="border p-2" placeholder="email" {...register('email')} />
        {errors.email && <span className="text-red-600 text-xs">{errors.email.message}</span>}

        <label className="text-sm">Password</label>
        <input className="border p-2" type="password" placeholder="password" {...register('password')} />
        {errors.password && <span className="text-red-600 text-xs">{errors.password.message}</span>}

        <button className="border px-3 py-2" disabled={isSubmitting} type="submit">{isSubmitting ? 'Creating...' : 'Create account'}</button>
        <a className="text-blue-600 text-sm" href="/login">Back to login</a>
      </form>
    </main>
  )
}


