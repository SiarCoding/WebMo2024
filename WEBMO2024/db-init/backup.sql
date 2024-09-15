--
-- PostgreSQL database dump
--

-- Dumped from database version 14.13 (Homebrew)
-- Dumped by pg_dump version 14.13 (Homebrew)

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
-- Name: food; Type: TABLE; Schema: public; Owner: sa
--

CREATE TABLE public.food (
    id integer NOT NULL,
    name character varying(255),
    price numeric(10,2) NOT NULL,
    type character varying(50) NOT NULL,
    CONSTRAINT essen_type_check CHECK (((type)::text = ANY ((ARRAY['vegan'::character varying, 'vegetarisch'::character varying, 'mit Fleisch'::character varying])::text[])))
);


ALTER TABLE public.food OWNER TO sa;

--
-- Name: essen_id_seq; Type: SEQUENCE; Schema: public; Owner: sa
--

CREATE SEQUENCE public.essen_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.essen_id_seq OWNER TO sa;

--
-- Name: essen_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sa
--

ALTER SEQUENCE public.essen_id_seq OWNED BY public.food.id;


--
-- Name: food_in_plan; Type: TABLE; Schema: public; Owner: sa
--

CREATE TABLE public.food_in_plan (
    id integer NOT NULL,
    plan_id integer NOT NULL,
    food_id integer NOT NULL,
    day_of_week character varying(10) NOT NULL
);


ALTER TABLE public.food_in_plan OWNER TO sa;

--
-- Name: food_in_plan_id_seq; Type: SEQUENCE; Schema: public; Owner: sa
--

CREATE SEQUENCE public.food_in_plan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.food_in_plan_id_seq OWNER TO sa;

--
-- Name: food_in_plan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sa
--

ALTER SEQUENCE public.food_in_plan_id_seq OWNED BY public.food_in_plan.id;


--
-- Name: foodplan; Type: TABLE; Schema: public; Owner: sa
--

CREATE TABLE public.foodplan (
    plan_id integer NOT NULL,
    week_number integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.foodplan OWNER TO sa;

--
-- Name: foodplan_plan_id_seq; Type: SEQUENCE; Schema: public; Owner: sa
--

CREATE SEQUENCE public.foodplan_plan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.foodplan_plan_id_seq OWNER TO sa;

--
-- Name: foodplan_plan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sa
--

ALTER SEQUENCE public.foodplan_plan_id_seq OWNED BY public.foodplan.plan_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: sa
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(50) NOT NULL
);


ALTER TABLE public.users OWNER TO sa;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: sa
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO sa;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: sa
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: food id; Type: DEFAULT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.food ALTER COLUMN id SET DEFAULT nextval('public.essen_id_seq'::regclass);


--
-- Name: food_in_plan id; Type: DEFAULT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.food_in_plan ALTER COLUMN id SET DEFAULT nextval('public.food_in_plan_id_seq'::regclass);


--
-- Name: foodplan plan_id; Type: DEFAULT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.foodplan ALTER COLUMN plan_id SET DEFAULT nextval('public.foodplan_plan_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: food; Type: TABLE DATA; Schema: public; Owner: sa
--

COPY public.food (id, name, price, type) FROM stdin;
40	Döner	5.00	vegan
49	Ei	2.00	vegetarisch
50	Gekochtes Ei	4.00	vegetarisch
52	Biryani	8.98	vegetarisch
51	Thai-Curry	4.92	vegan
42	Fleischball	7.15	mit Fleisch
45	Suppe	10.00	vegetarisch
48	Vegane Pizza	5.99	vegan
44	Hot-Dog	9.99	mit Fleisch
41	Mozzerella-Sticks	8.03	vegan
\.


--
-- Data for Name: food_in_plan; Type: TABLE DATA; Schema: public; Owner: sa
--

COPY public.food_in_plan (id, plan_id, food_id, day_of_week) FROM stdin;
181	17	51	Montag
182	17	45	Dienstag
183	17	42	Mittwoch
184	17	41	Donnerstag
185	17	50	Freitag
\.


--
-- Data for Name: foodplan; Type: TABLE DATA; Schema: public; Owner: sa
--

COPY public.foodplan (plan_id, week_number, created_at) FROM stdin;
17	1	2024-09-15 02:53:49.7376
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: sa
--

COPY public.users (id, username, password, role) FROM stdin;
1	admin	admin123	admin
2	user	user123	user
\.


--
-- Name: essen_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sa
--

SELECT pg_catalog.setval('public.essen_id_seq', 52, true);


--
-- Name: food_in_plan_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sa
--

SELECT pg_catalog.setval('public.food_in_plan_id_seq', 185, true);


--
-- Name: foodplan_plan_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sa
--

SELECT pg_catalog.setval('public.foodplan_plan_id_seq', 17, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: sa
--

SELECT pg_catalog.setval('public.users_id_seq', 1, true);


--
-- Name: food essen_pkey; Type: CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.food
    ADD CONSTRAINT essen_pkey PRIMARY KEY (id);


--
-- Name: food_in_plan food_in_plan_pkey; Type: CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.food_in_plan
    ADD CONSTRAINT food_in_plan_pkey PRIMARY KEY (id);


--
-- Name: foodplan foodplan_pkey; Type: CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.foodplan
    ADD CONSTRAINT foodplan_pkey PRIMARY KEY (plan_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: food_in_plan food_in_plan_food_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.food_in_plan
    ADD CONSTRAINT food_in_plan_food_id_fkey FOREIGN KEY (food_id) REFERENCES public.food(id);


--
-- Name: food_in_plan food_in_plan_plan_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: sa
--

ALTER TABLE ONLY public.food_in_plan
    ADD CONSTRAINT food_in_plan_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES public.foodplan(plan_id);


--
-- PostgreSQL database dump complete
--

