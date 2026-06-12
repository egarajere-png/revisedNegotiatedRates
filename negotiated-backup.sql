--
-- PostgreSQL database dump
--

\restrict Y8ataWjjGPiBaCDzue41bCFkiUfW7GtvGP8snupeXURaYpIiZhQVTCUVaAvEeFT

-- Dumped from database version 14.23 (Debian 14.23-1.pgdg13+1)
-- Dumped by pg_dump version 14.23 (Debian 14.23-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: rate_request; Type: TABLE; Schema: public; Owner: abconnect
--

CREATE TABLE public.rate_request (
    id integer NOT NULL,
    appeal boolean NOT NULL,
    created_on timestamp without time zone,
    cust_id character varying(255),
    customer_name character varying(255),
    destination_currency character varying(255),
    editted_on timestamp without time zone,
    granted_amount_limit double precision,
    granted_by character varying(255),
    granted_rate double precision,
    notification_email character varying(255),
    requested_amount_limit double precision,
    requested_by character varying(255),
    requested_rate double precision,
    source_currency character varying(255),
    status smallint NOT NULL,
    transfer_type character varying(255),
    uuid character varying(255)
);


ALTER TABLE public.rate_request OWNER TO abconnect;

--
-- Name: rate_request_id_seq; Type: SEQUENCE; Schema: public; Owner: abconnect
--

CREATE SEQUENCE public.rate_request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rate_request_id_seq OWNER TO abconnect;

--
-- Name: rate_request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: abconnect
--

ALTER SEQUENCE public.rate_request_id_seq OWNED BY public.rate_request.id;


--
-- Name: transfer; Type: TABLE; Schema: public; Owner: abconnect
--

CREATE TABLE public.transfer (
    id integer NOT NULL,
    amount double precision,
    created_by character varying(255),
    created_on timestamp without time zone,
    destination_currency character varying(255),
    editted_on timestamp without time zone,
    recipient_address character varying(255),
    recipient_bank character varying(255),
    recipient_bank_address character varying(255),
    recipient_name character varying(255),
    source_account character varying(255),
    source_currency character varying(255),
    status smallint NOT NULL,
    transfer_type character varying(255),
    rate_request_id integer
);


ALTER TABLE public.transfer OWNER TO abconnect;

--
-- Name: transfer_id_seq; Type: SEQUENCE; Schema: public; Owner: abconnect
--

CREATE SEQUENCE public.transfer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.transfer_id_seq OWNER TO abconnect;

--
-- Name: transfer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: abconnect
--

ALTER SEQUENCE public.transfer_id_seq OWNED BY public.transfer.id;


--
-- Name: rate_request id; Type: DEFAULT; Schema: public; Owner: abconnect
--

ALTER TABLE ONLY public.rate_request ALTER COLUMN id SET DEFAULT nextval('public.rate_request_id_seq'::regclass);


--
-- Name: transfer id; Type: DEFAULT; Schema: public; Owner: abconnect
--

ALTER TABLE ONLY public.transfer ALTER COLUMN id SET DEFAULT nextval('public.transfer_id_seq'::regclass);


--
-- Data for Name: rate_request; Type: TABLE DATA; Schema: public; Owner: abconnect
--

COPY public.rate_request (id, appeal, created_on, cust_id, customer_name, destination_currency, editted_on, granted_amount_limit, granted_by, granted_rate, notification_email, requested_amount_limit, requested_by, requested_rate, source_currency, status, transfer_type, uuid) FROM stdin;
1	f	2026-06-11 10:41:16.249	CUST001	egarajere	KES	2026-06-11 10:41:16.249	\N	\N	\N	egarajere@gmail.com	500000	egarajere	130.5	USD	0	INTERNATIONAL	18579ca5-e98a-4fea-a177-7a82fe11e14b
\.


--
-- Data for Name: transfer; Type: TABLE DATA; Schema: public; Owner: abconnect
--

COPY public.transfer (id, amount, created_by, created_on, destination_currency, editted_on, recipient_address, recipient_bank, recipient_bank_address, recipient_name, source_account, source_currency, status, transfer_type, rate_request_id) FROM stdin;
\.


--
-- Name: rate_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: abconnect
--

SELECT pg_catalog.setval('public.rate_request_id_seq', 1, true);


--
-- Name: transfer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: abconnect
--

SELECT pg_catalog.setval('public.transfer_id_seq', 1, false);


--
-- Name: rate_request rate_request_pkey; Type: CONSTRAINT; Schema: public; Owner: abconnect
--

ALTER TABLE ONLY public.rate_request
    ADD CONSTRAINT rate_request_pkey PRIMARY KEY (id);


--
-- Name: transfer transfer_pkey; Type: CONSTRAINT; Schema: public; Owner: abconnect
--

ALTER TABLE ONLY public.transfer
    ADD CONSTRAINT transfer_pkey PRIMARY KEY (id);


--
-- Name: transfer fkqi5vaueerw222nwn7bb5nrnnq; Type: FK CONSTRAINT; Schema: public; Owner: abconnect
--

ALTER TABLE ONLY public.transfer
    ADD CONSTRAINT fkqi5vaueerw222nwn7bb5nrnnq FOREIGN KEY (rate_request_id) REFERENCES public.rate_request(id);


--
-- PostgreSQL database dump complete
--

\unrestrict Y8ataWjjGPiBaCDzue41bCFkiUfW7GtvGP8snupeXURaYpIiZhQVTCUVaAvEeFT

