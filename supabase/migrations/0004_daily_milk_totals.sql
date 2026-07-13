-- Farm-wide daily milk total (separate from per-cow milk_records), for farmers
-- who prefer logging one total instead of per-cow yields.

create table public.daily_milk_totals (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  date date not null,
  morning_total numeric,
  evening_total numeric,
  updated_at timestamptz not null default now(),
  unique (owner_id, date)
);
alter table public.daily_milk_totals enable row level security;
create index daily_milk_totals_owner_id_idx on public.daily_milk_totals (owner_id);
create policy "owner_full_access" on public.daily_milk_totals
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));
