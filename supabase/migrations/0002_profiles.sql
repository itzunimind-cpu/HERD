-- Adds a per-farmer profile (name, phone, farm name) for the new Dashboard screen.
-- One row per auth.users row, keyed by the user's own id.

create table public.profiles (
  id uuid primary key references auth.users(id) default auth.uid(),
  name text,
  phone text,
  farm_name text,
  updated_at timestamptz not null default now()
);
alter table public.profiles enable row level security;
create policy "owner_full_access" on public.profiles
  for all using (id = (select auth.uid())) with check (id = (select auth.uid()));
