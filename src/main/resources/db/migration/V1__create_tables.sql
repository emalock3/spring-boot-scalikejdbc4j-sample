create table company (
  id serial not null,
  name varchar(100)
);

create table programmer (
  id serial not null,
  github_name varchar(100) not null,
  real_name varchar(100),
  company_id bigint references company(id),
  created_at timestamp not null default current_timestamp
);
