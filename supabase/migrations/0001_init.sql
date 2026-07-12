-- Consolidated schema for the Herd Supabase project (thitdaiznbuxkejvitex).
-- This mirrors what was already applied live via the Supabase MCP `apply_migration`
-- tool during scaffolding - kept here as a checked-in record, not re-run automatically.

create table public.cows (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  tag text not null,
  name text,
  breed text,
  birth_date date,
  gender text,
  weight numeric,
  lineage_tag text,
  photo_uri text,
  updated_at timestamptz not null default now(),
  unique (owner_id, tag)
);
alter table public.cows enable row level security;
create index cows_owner_id_idx on public.cows (owner_id);
create policy "owner_full_access" on public.cows
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.breeding_info (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  pregnancy_status text,
  last_heat_date date,
  insemination_date date,
  expected_calving_date date,
  updated_at timestamptz not null default now(),
  unique (cow_id)
);
alter table public.breeding_info enable row level security;
create index breeding_info_cow_id_idx on public.breeding_info (cow_id);
create index breeding_info_owner_id_idx on public.breeding_info (owner_id);
create policy "owner_full_access" on public.breeding_info
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.calving_history (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  calf_sex text,
  calving_date date,
  updated_at timestamptz not null default now()
);
alter table public.calving_history enable row level security;
create index calving_history_cow_id_idx on public.calving_history (cow_id);
create index calving_history_owner_id_idx on public.calving_history (owner_id);
create policy "owner_full_access" on public.calving_history
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.milk_records (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  date date not null,
  morning_yield numeric,
  evening_yield numeric,
  fat_pct numeric,
  snf_pct numeric,
  updated_at timestamptz not null default now(),
  unique (cow_id, date)
);
alter table public.milk_records enable row level security;
create index milk_records_cow_id_idx on public.milk_records (cow_id);
create index milk_records_owner_id_idx on public.milk_records (owner_id);
create policy "owner_full_access" on public.milk_records
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.health_status (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  current_status text,
  updated_at timestamptz not null default now(),
  unique (cow_id)
);
alter table public.health_status enable row level security;
create index health_status_cow_id_idx on public.health_status (cow_id);
create index health_status_owner_id_idx on public.health_status (owner_id);
create policy "owner_full_access" on public.health_status
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.vaccinations (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  vaccine_name text,
  date date,
  updated_at timestamptz not null default now()
);
alter table public.vaccinations enable row level security;
create index vaccinations_cow_id_idx on public.vaccinations (cow_id);
create index vaccinations_owner_id_idx on public.vaccinations (owner_id);
create policy "owner_full_access" on public.vaccinations
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.illness_log (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  description text,
  date date,
  treatment text,
  updated_at timestamptz not null default now()
);
alter table public.illness_log enable row level security;
create index illness_log_cow_id_idx on public.illness_log (cow_id);
create index illness_log_owner_id_idx on public.illness_log (owner_id);
create policy "owner_full_access" on public.illness_log
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));

create table public.daily_logs (
  id uuid primary key default gen_random_uuid(),
  owner_id uuid not null references auth.users(id) default auth.uid(),
  cow_id uuid not null references public.cows(id) on delete cascade,
  date date not null,
  feed text,
  water text,
  temperature numeric,
  activity_notes text,
  updated_at timestamptz not null default now(),
  unique (cow_id, date)
);
alter table public.daily_logs enable row level security;
create index daily_logs_cow_id_idx on public.daily_logs (cow_id);
create index daily_logs_owner_id_idx on public.daily_logs (owner_id);
create policy "owner_full_access" on public.daily_logs
  for all using (owner_id = (select auth.uid())) with check (owner_id = (select auth.uid()));
