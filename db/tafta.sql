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
	legal_type varchar(255) NOT NULL DEFAULT 'limited_company'::character varying,
	status varchar(255) NOT NULL DEFAULT 'ÁCTIVE'::character varying,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	api_password varchar NULL,
	api_key varchar NULL,
	api_access bool NULL DEFAULT false,
	created_by varchar NULL,
	updated_by varchar NULL,
	CONSTRAINT companies_pkey PRIMARY KEY (id),
	CONSTRAINT company_id_unique UNIQUE (company_id)
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

INSERT INTO public.permissions ("action",description,created_at,updated_at,created_by,updated_by,status,deleted_at) VALUES
	 ('MODIFY USER','modify user','2023-10-14 14:52:48.953392','2023-10-14 14:52:48.953392','admin','admin','ACTIVE',NULL),
	 ('DELETE USER','modify user','2023-10-14 14:52:55.953769','2023-10-14 14:52:55.953769','admin','admin','ACTIVE',NULL),
	 ('ADD USER','create another user','2023-10-13 17:41:33.564354','2023-10-16 11:48:14.188818','admin','admin','ACTIVE','2023-10-14 08:52:29.127552'),
	 ('CREATE PROPERTY','create property','2023-10-16 11:38:28.911974','2023-10-16 11:52:29.609384','admin','admin','DELETED','2023-10-16 11:52:29.609384');

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

INSERT INTO public.roles ("name",description,permissions,created_at,updated_at,created_by,updated_by,status,deleted_at) VALUES
	 ('ROLE 1','Role 1','1,2','2023-10-16 09:41:39.685038','2023-10-16 11:00:50.529533','admin','admin','ACTIVE',NULL),
	 ('ROLE 2','Role 2','1,3','2023-10-16 11:09:25.114266','2023-10-16 11:21:22.914156','admin','admin','ACTIVE','2023-10-16 11:21:22.914156');

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
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	published_at varchar NULL,
	created_by varchar NULL,
	updated_by varchar NULL,
	title varchar(255) NOT NULL,
	category varchar(255) NOT NULL,
	property_type varchar(255) NULL,
	owner_type varchar(255) NULL,
	country varchar(255) NULL,
	city varchar(255) NULL,
	county varchar(255) NULL,
	postal_code varchar(255) NULL,
	address varchar(255) NULL,
	latitude varchar(255) NULL,
	longitude varchar(255) NULL,
	property_location varchar(255) NULL,
	floor_area varchar(255) NULL DEFAULT '0'::character varying,
	floor_area_unit varchar(255) NULL DEFAULT 'square metres'::character varying,
	bedrooms varchar(255) NULL,
	bathrooms varchar(255) NULL,
	parking_spots varchar(255) NULL,
	amenities varchar NULL,
	auto_confirm bool NOT NULL DEFAULT false,
	are_pets_allowed bool NOT NULL DEFAULT false,
	pets_allowed varchar(255) NULL,
	property_description text NULL,
	currency varchar(255) NULL DEFAULT 'KES'::character varying,
	price varchar(255) NOT NULL,
	cost_period varchar(255) NULL DEFAULT 'per month'::character varying,
	price_range bool NOT NULL DEFAULT false,
	minimum_price varchar(255) NULL,
	maximum_price varchar(255) NULL,
	contact_first_name varchar(255) NOT NULL,
	contact_second_name varchar(255) NOT NULL,
	contact_email varchar(255) NOT NULL,
	contact_phone varchar(255) NOT NULL,
	company varchar(255) NULL,
	verified bool NOT NULL DEFAULT false,
	status varchar(255) NOT NULL DEFAULT 'ACTIVE'::character varying,
	property_id varchar(255) NOT NULL,
	metadata text NULL,
	CONSTRAINT properties_pkey PRIMARY KEY (id),
	CONSTRAINT property_id_unique UNIQUE (property_id)
);

INSERT INTO public.properties (property_name,created_at,deleted_at,updated_at,published_at,created_by,updated_by,property_description,company,verified,status,property_id,metadata,county,"location",latitude,longitude,price,property_type,minimum_price,maximum_price,property_amenities) VALUES
	 ('prop name','2023-10-12 13:40:51.513305',NULL,'2023-10-12 13:40:51.513305','2023-10-12 13:40:51.513305',NULL,NULL,'Spacious apartment',NULL,false,'ACTIVE','5ClxQpHbQw',NULL,'Kirinyaga','Kutus town','24.22144','0.46685','5000','apartment','2000','5500',NULL),
	 ('Kamukunji house','2023-10-12 18:40:44.514829',NULL,'2023-10-12 18:40:44.514829','2023-10-12 18:40:44.514829',NULL,NULL,'Spacious apartment','15',false,'ACTIVE','fu0oUVG27W',NULL,'Kiambu','Githunguri town','24.22144','0.46685','5000','apartment','5000','10000','cctv, parking'),
	 ('prop name','2023-10-12 14:26:59.779419','2023-10-12 18:50:28.932812','2023-10-12 14:26:59.779419','2023-10-12 14:26:59.779419',NULL,'admin','Spacious apartment',NULL,false,'DELETED','BLk9iqYWDJ',NULL,'Kirinyaga','Kutus town','24.22144','0.46685','5000','apartment',NULL,'1000',NULL);


-- public.photos definition

-- Drop table

-- DROP TABLE photos;

CREATE TABLE photos (
	id serial4 NOT NULL,
	parent_id varchar(255) NOT NULL,
	photo_id varchar(255) NOT NULL,
	created_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	published_at varchar NULL,
	created_by varchar NULL,
	updated_by varchar NULL,
	file_name varchar(255) NOT NULL,
    alternative_text varchar(255) NULL,
	caption text NULL,
    width varchar NULL,
    height varchar NULL,
    file_format varchar NULL,
    hash text NULL,
    ext varchar(255) NULL,
    mime varchar(255) NULL,
    file_size varchar NOT NULL,
	CONSTRAINT property_photos_pkey PRIMARY KEY (id),
	CONSTRAINT photo_id_unique UNIQUE (photo_id)
);

-- public.files definition

-- Drop table

-- DROP TABLE files;

CREATE TABLE files (
	id serial4 NOT NULL,
	file_name varchar(255) NOT NULL,
	alternative_text varchar(255) NULL,
	caption text NULL,
	width int4 NULL,
	height int4 NULL,
	file_format varchar NULL,
	hash text NULL,
	ext varchar(255) NULL,
	mime varchar(255) NULL,
	file_size varchar NOT NULL,
	url varchar(255) NULL,
	preview_url varchar(255) NULL,
	provider varchar(255) NULL,
	provider_metadata text NULL,
	folder_path text NULL,
	created_at timestamp(6) NULL,
	updated_at timestamp(6) NULL,
	deleted_at timestamp(6) NULL,
	created_by varchar NULL,
    updated_by varchar NULL,
	CONSTRAINT files_pkey PRIMARY KEY (id)
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
	phone_number varchar(255) NULL,
	status varchar(255) NOT NULL DEFAULT 'ACTIVE'::character varying,
	created_at timestamp(6) NOT NULL DEFAULT now(),
	updated_at timestamp(6) NOT NULL DEFAULT now(),
	deleted_at timestamp(6) NULL,
	reset_password bool NULL DEFAULT true,
	api_key varchar NULL,
	api_password varchar NULL,
	api_access bool NULL DEFAULT false,
	CONSTRAINT users_email_unique UNIQUE (email),
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

INSERT INTO public.users (company_id,role_id,fullname,email,"password",auth_channel,phone_number,status,created_at,updated_at,deleted_at,reset_password) VALUES
	 (NULL,'1','Simon Ngigi','sgatharikih@gmail.com',NULL,NULL,'07017856421','DELETED','2023-10-25 20:10:28.344693','2023-10-25 20:40:48.460764','2023-10-25 20:40:48.460764',true);
