-- Adds pregnancy-test-date and semen/breed-type tracking to breeding_info,
-- feeding the new heat-cycle reminder logic.

alter table public.breeding_info
  add column pregnancy_test_date date,
  add column semen_breed text;
