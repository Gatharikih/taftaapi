-- public.companies definition

-- Drop table

-- DROP TABLE companies;

CREATE TABLE companies (
	id serial4 NOT NULL,
	company_id varchar(255) NOT NULL,
	"password" varchar(255) NOT NULL,
	company_name varchar(255) NULL,
	company_description text NULL,
	company_address varchar(255) NULL,
	company_email varchar(255) NULL,
	contact_person varchar(255) NULL,
	status varchar(255) NOT NULL DEFAULT 'ÁCTIVE'::character varying,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	published_at varchar NULL,
	api_password varchar NULL,
	api_key varchar NULL,
	api_access bool NULL DEFAULT false,
	created_by varchar NULL,
	updated_by varchar NULL,
	CONSTRAINT company_id_unique UNIQUE (company_id),
	CONSTRAINT companies_pkey PRIMARY KEY (id)
);

-- public.files definition

-- Drop table

-- DROP TABLE files;

CREATE TABLE files (
	id serial4 NOT NULL,
	"name" varchar(255) NULL,
	alternative_text varchar(255) NULL,
	caption varchar(255) NULL,
	width int4 NULL,
	height int4 NULL,
	formats varchar NULL,
	hash varchar(255) NULL,
	ext varchar(255) NULL,
	mime varchar(255) NULL,
	"size" varchar NULL,
	url varchar(255) NULL,
	preview_url varchar(255) NULL,
	provider varchar(255) NULL,
	provider_metadata text NULL,
	folder_path varchar(255) NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	created_by varchar NULL,
    updated_by varchar NULL,
	CONSTRAINT files_pkey PRIMARY KEY (id)
);

-- public.permissions definition

-- Drop table

-- DROP TABLE permissions;

CREATE TABLE permissions (
	id serial4 NOT NULL,
	"action" varchar(255) NOT NULL,
	description varchar(255) NULL,
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now(),
	created_by varchar(255) NULL,
	updated_by varchar(255) NULL,
	status varchar(255) NOT NULL DEFAULT 'ACTIVE'::character varying,
	deleted_at timestamp NULL,
	CONSTRAINT permissions_action_unique UNIQUE ("action"),
	CONSTRAINT permissions_pkey PRIMARY KEY (id)
);


-- public.promotions definition

-- Drop table

-- DROP TABLE promotions;

CREATE TABLE promotions (
	id serial4 NOT NULL,
	promotion_id varchar(255) NOT NULL,
	promotion_name varchar(255) NOT NULL,
	promotion_description text NULL,
	promotion_code varchar(255) NULL,
	status varchar(255) NOT NULL DEFAULT 'IN-ACTIVE'::character varying,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	published_at timestamp(6) NULL,
    created_by varchar NULL,
	updated_by varchar NULL,
	CONSTRAINT promotions_pkey PRIMARY KEY (id),
	CONSTRAINT promotion_id_unique UNIQUE (promotion_id)
);

-- public.properties definition

-- Drop table

-- DROP TABLE properties;

CREATE TABLE properties (
	id serial4 NOT NULL,
	property_name varchar(255) NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	published_at varchar NULL,
	created_by varchar NULL,
	updated_by varchar NULL,
	property_description text NULL,
	company varchar NULL,
	verified bool NOT NULL DEFAULT false,
	status varchar(255) NOT NULL DEFAULT 'ACTIVE'::character varying,
	property_id varchar(255) NOT NULL,
	metadata varchar NULL,
	county varchar(255) NULL,
	"location" varchar(255) NULL,
	latitude varchar(255) NULL,
	longitude varchar(255) NULL,
	property_price varchar(255) NULL,
	property_type varchar(255) NULL,
	property_amenities varchar NULL,
	minimum_price varchar(255) NULL,
	maximum_price varchar(255) NULL,
	CONSTRAINT properties_pkey PRIMARY KEY (id),
	CONSTRAINT property_id_unique UNIQUE (property_id)
);

-- public.roles definition

-- Drop table

-- DROP TABLE roles;

CREATE TABLE roles (
	id serial4 NOT NULL,
	"name" varchar(255) NOT NULL,
	description varchar(255) NULL,
	permissions varchar NOT NULL,
	created_at timestamp NOT NULL DEFAULT now(),
	updated_at timestamp NOT NULL DEFAULT now(),
	created_by varchar(255) NULL,
	updated_by varchar(255) NULL,
	status varchar(255) NOT NULL DEFAULT 'ACTIVE'::character varying,
	deleted_at timestamp NULL,
	CONSTRAINT roles_name_unique UNIQUE (name),
	CONSTRAINT roles_pkey PRIMARY KEY (id)
);


-- public.users definition

-- Drop table

-- DROP TABLE users;

CREATE TABLE users (
	id serial4 NOT NULL,
	company_id varchar(255) NULL,
	role_id varchar(255) NOT NULL,
	fullname varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	"password" varchar(255) NULL,
	auth_channel varchar(255) NULL,
	msisdn varchar(255) NULL,
	status varchar(255) NOT NULL DEFAULT 'ACTIVE'::character varying,
	created_at timestamp(6) NOT NULL DEFAULT now(),
	updated_at timestamp(6) NOT NULL DEFAULT now(),
	deleted_at timestamp(6) NULL,
	reset_password bool NULL DEFAULT true,
	api_password varchar NULL,
	api_access bool NULL DEFAULT false,
	CONSTRAINT users_email_unique UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);