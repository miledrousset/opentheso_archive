--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.9
-- Dumped by pg_dump version 10.4

-- Started on 2018-08-02 11:45:25 CEST

-- version=4.3.8

SET role = opentheso;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 1 (class 3079 OID 12655)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

--CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 3042 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

--COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 561 (class 1247 OID 28890)
-- Name: alignement_format; Type: TYPE; Schema: public; Owner: opentheso
--

CREATE TYPE public.alignement_format AS ENUM (
    'skos',
    'json',
    'xml'
);


ALTER TYPE public.alignement_format OWNER TO opentheso;

--
-- TOC entry 564 (class 1247 OID 28898)
-- Name: alignement_type_rqt; Type: TYPE; Schema: public; Owner: opentheso
--

CREATE TYPE public.alignement_type_rqt AS ENUM (
    'SPARQL',
    'REST'
);


ALTER TYPE public.alignement_type_rqt OWNER TO opentheso;

--
-- TOC entry 567 (class 1247 OID 28904)
-- Name: auth_method; Type: TYPE; Schema: public; Owner: opentheso
--

CREATE TYPE public.auth_method AS ENUM (
    'DB',
    'LDAP',
    'FILE',
    'test'
);


ALTER TYPE public.auth_method OWNER TO opentheso;

--
-- TOC entry 280 (class 1255 OID 109098)
-- Name: alter_table_concept_group_addcolumn_id_handle(); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.alter_table_concept_group_addcolumn_id_handle() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
    IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_name='concept_group' AND column_name='id_handle' ) THEN
        execute 'ALTER TABLE concept_group ADD COLUMN id_handle character varying DEFAULT ''''::character varying;';
    END IF;
end;
$$;


ALTER FUNCTION public.alter_table_concept_group_addcolumn_id_handle() OWNER TO opentheso;

--
-- TOC entry 278 (class 1255 OID 109049)
-- Name: changeconstraintalignement(); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.changeconstraintalignement() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
	if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_uri_target_internal_id_thesaurus_internal_id_con_key') then 
	execute
	'
            alter table alignement
            drop constraint alignement_uri_target_internal_id_thesaurus_internal_id_con_key;
        alter table alignement 
            add constraint  alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key unique 
            (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type)
        ';
        end if;
        if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_id_alignem') then 
	execute
	'
            alter table alignement
            drop constraint alignement_internal_id_concept_internal_id_thesaurus_id_alignem;
        ';
        end if;
        if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key') then 
	execute
	'
            alter table alignement
            drop constraint alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key;
        ';
        end if;

        if not exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key2') then 
	execute
	'
            alter table alignement
            add constraint  alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key2 unique 
            (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type, uri_target)
        ';
        end if;
  end;
  $$;


ALTER FUNCTION public.changeconstraintalignement() OWNER TO opentheso;

--
-- TOC entry 279 (class 1255 OID 109097)
-- Name: unaccent_string(text); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.unaccent_string(text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
DECLARE
input_string text := $1;
BEGIN

input_string := translate(input_string, 'âãäåāăąÁÂÃÄÅĀĂĄ', 'aaaaaaaaaaaaaaa');
input_string := translate(input_string, 'èééêëēĕėęěĒĔĖĘĚÉ', 'eeeeeeeeeeeeeeee');
input_string := translate(input_string, 'ìíîïìĩīĭÌÍÎÏÌĨĪĬ', 'iiiiiiiiiiiiiiii');
input_string := translate(input_string, 'óôõöōŏőÒÓÔÕÖŌŎŐ', 'ooooooooooooooo');
input_string := translate(input_string, 'ùúûüũūŭůÙÚÛÜŨŪŬŮ', 'uuuuuuuuuuuuuuuu');
input_string := translate(input_string, '-_/()', '     ');

return input_string;
END;
$_$;


ALTER FUNCTION public.unaccent_string(text) OWNER TO opentheso;

--
-- TOC entry 185 (class 1259 OID 28915)
-- Name: alignement_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.alignement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alignement_id_seq OWNER TO opentheso;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 186 (class 1259 OID 28917)
-- Name: alignement; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.alignement (
    id integer DEFAULT nextval('public.alignement_id_seq'::regclass) NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    author integer,
    concept_target character varying,
    thesaurus_target character varying,
    uri_target character varying,
    alignement_id_type integer NOT NULL,
    internal_id_thesaurus character varying NOT NULL,
    internal_id_concept character varying,
    id_alignement_source integer
);


ALTER TABLE public.alignement OWNER TO opentheso;

--
-- TOC entry 187 (class 1259 OID 28926)
-- Name: alignement_preferences_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.alignement_preferences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alignement_preferences_id_seq OWNER TO opentheso;

--
-- TOC entry 188 (class 1259 OID 28928)
-- Name: alignement_preferences; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.alignement_preferences (
    id integer DEFAULT nextval('public.alignement_preferences_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_user integer NOT NULL,
    id_concept_depart character varying NOT NULL,
    id_concept_tratees character varying,
    id_alignement_source integer NOT NULL
);


ALTER TABLE public.alignement_preferences OWNER TO opentheso;

--
-- TOC entry 189 (class 1259 OID 28935)
-- Name: alignement_source__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.alignement_source__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alignement_source__id_seq OWNER TO opentheso;

--
-- TOC entry 190 (class 1259 OID 28937)
-- Name: alignement_source; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.alignement_source (
    source character varying,
    requete character varying,
    type_rqt public.alignement_type_rqt NOT NULL,
    alignement_format public.alignement_format NOT NULL,
    id integer DEFAULT nextval('public.alignement_source__id_seq'::regclass) NOT NULL,
    id_user integer,
    description character varying,
    gps boolean DEFAULT false
);


ALTER TABLE public.alignement_source OWNER TO opentheso;

--
-- TOC entry 191 (class 1259 OID 28945)
-- Name: alignement_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.alignement_type (
    id integer NOT NULL,
    label text NOT NULL,
    isocode text NOT NULL,
    label_skos character varying
);


ALTER TABLE public.alignement_type OWNER TO opentheso;

--
-- TOC entry 192 (class 1259 OID 28951)
-- Name: compound_equivalence; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.compound_equivalence (
    id_split_nonpreferredterm text NOT NULL,
    id_preferredterm text NOT NULL
);


ALTER TABLE public.compound_equivalence OWNER TO opentheso;

--
-- TOC entry 193 (class 1259 OID 28957)
-- Name: concept__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept__id_seq
    START WITH 43
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept__id_seq OWNER TO opentheso;

--
-- TOC entry 194 (class 1259 OID 28959)
-- Name: concept; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_ark character varying DEFAULT ''::character varying,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    status character varying,
    notation character varying DEFAULT ''::character varying,
    top_concept boolean,
    id integer DEFAULT nextval('public.concept__id_seq'::regclass),
    gps boolean DEFAULT false,
    id_handle character varying DEFAULT ''::character varying
);


ALTER TABLE public.concept OWNER TO opentheso;

--
-- TOC entry 195 (class 1259 OID 28972)
-- Name: concept_candidat__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept_candidat__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept_candidat__id_seq OWNER TO opentheso;

--
-- TOC entry 196 (class 1259 OID 28974)
-- Name: concept_candidat; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_candidat (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    status character varying DEFAULT 'a'::character varying,
    id integer DEFAULT nextval('public.concept_candidat__id_seq'::regclass),
    admin_message character varying,
    admin_id integer
);


ALTER TABLE public.concept_candidat OWNER TO opentheso;

--
-- TOC entry 197 (class 1259 OID 28984)
-- Name: concept_fusion; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_fusion (
    id_concept1 character varying NOT NULL,
    id_concept2 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.concept_fusion OWNER TO opentheso;

--
-- TOC entry 198 (class 1259 OID 28991)
-- Name: concept_group__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept_group__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept_group__id_seq OWNER TO opentheso;

--
-- TOC entry 199 (class 1259 OID 28993)
-- Name: concept_group; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group (
    idgroup text NOT NULL,
    id_ark text NOT NULL,
    idthesaurus text NOT NULL,
    idtypecode text DEFAULT 'MT'::text NOT NULL,
    notation text,
    id integer DEFAULT nextval('public.concept_group__id_seq'::regclass) NOT NULL,
    numerotation integer,
    id_handle character varying DEFAULT ''::character varying
);


ALTER TABLE public.concept_group OWNER TO opentheso;

--
-- TOC entry 200 (class 1259 OID 29001)
-- Name: concept_group_concept; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_concept (
    idgroup text NOT NULL,
    idthesaurus text NOT NULL,
    idconcept text NOT NULL
);


ALTER TABLE public.concept_group_concept OWNER TO opentheso;

--
-- TOC entry 201 (class 1259 OID 29007)
-- Name: concept_group_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept_group_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept_group_historique__id_seq OWNER TO opentheso;

--
-- TOC entry 202 (class 1259 OID 29009)
-- Name: concept_group_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_historique (
    idgroup text NOT NULL,
    id_ark text NOT NULL,
    idthesaurus text NOT NULL,
    idtypecode text NOT NULL,
    idparentgroup text,
    notation text,
    idconcept text,
    id integer DEFAULT nextval('public.concept_group_historique__id_seq'::regclass) NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.concept_group_historique OWNER TO opentheso;

--
-- TOC entry 203 (class 1259 OID 29017)
-- Name: concept_group_label_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept_group_label_id_seq
    START WITH 60
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept_group_label_id_seq OWNER TO opentheso;

--
-- TOC entry 204 (class 1259 OID 29019)
-- Name: concept_group_label; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_label (
    id integer DEFAULT nextval('public.concept_group_label_id_seq'::regclass) NOT NULL,
    lexicalvalue text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    lang character varying(5) NOT NULL,
    idthesaurus text NOT NULL,
    idgroup text NOT NULL
);


ALTER TABLE public.concept_group_label OWNER TO opentheso;

--
-- TOC entry 205 (class 1259 OID 29028)
-- Name: concept_group_label_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept_group_label_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept_group_label_historique__id_seq OWNER TO opentheso;

--
-- TOC entry 206 (class 1259 OID 29030)
-- Name: concept_group_label_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_label_historique (
    id integer DEFAULT nextval('public.concept_group_label_historique__id_seq'::regclass) NOT NULL,
    lexicalvalue text NOT NULL,
    modified timestamp(6) without time zone DEFAULT now() NOT NULL,
    lang character varying(5) NOT NULL,
    idthesaurus text NOT NULL,
    idgroup text NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.concept_group_label_historique OWNER TO opentheso;

--
-- TOC entry 207 (class 1259 OID 29038)
-- Name: concept_group_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_type (
    code text NOT NULL,
    label text NOT NULL,
    skoslabel text
);


ALTER TABLE public.concept_group_type OWNER TO opentheso;

--
-- TOC entry 208 (class 1259 OID 29044)
-- Name: concept_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.concept_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.concept_historique__id_seq OWNER TO opentheso;

--
-- TOC entry 209 (class 1259 OID 29046)
-- Name: concept_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_historique (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_ark character varying,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    status character varying,
    notation character varying DEFAULT ''::character varying,
    top_concept boolean,
    id_group character varying NOT NULL,
    id integer DEFAULT nextval('public.concept_historique__id_seq'::regclass),
    id_user integer NOT NULL
);


ALTER TABLE public.concept_historique OWNER TO opentheso;

--
-- TOC entry 210 (class 1259 OID 29055)
-- Name: concept_orphan; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_orphan (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.concept_orphan OWNER TO opentheso;

--
-- TOC entry 211 (class 1259 OID 29061)
-- Name: concept_term_candidat; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_term_candidat (
    id_concept character varying NOT NULL,
    id_term character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.concept_term_candidat OWNER TO opentheso;

--
-- TOC entry 212 (class 1259 OID 29067)
-- Name: copyright; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.copyright (
    id_thesaurus character varying NOT NULL,
    copyright character varying
);


ALTER TABLE public.copyright OWNER TO opentheso;

--
-- TOC entry 213 (class 1259 OID 29073)
-- Name: custom_concept_attribute; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.custom_concept_attribute (
    "idConcept" character varying NOT NULL,
    "lexicalValue" character varying,
    "customAttributeType" character varying,
    lang character varying
);


ALTER TABLE public.custom_concept_attribute OWNER TO opentheso;

--
-- TOC entry 214 (class 1259 OID 29079)
-- Name: custom_term_attribute; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.custom_term_attribute (
    identifier character varying NOT NULL,
    "lexicalValue" character varying,
    "customAttributeType" character varying,
    lang character varying
);


ALTER TABLE public.custom_term_attribute OWNER TO opentheso;

--
-- TOC entry 215 (class 1259 OID 29085)
-- Name: facet_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.facet_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.facet_id_seq OWNER TO opentheso;

--
-- TOC entry 216 (class 1259 OID 29087)
-- Name: gps; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.gps (
    id_concept character varying NOT NULL,
    id_theso character varying NOT NULL,
    latitude double precision,
    longitude double precision
);


ALTER TABLE public.gps OWNER TO opentheso;

--
-- TOC entry 217 (class 1259 OID 29093)
-- Name: gps_preferences_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.gps_preferences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.gps_preferences_id_seq OWNER TO opentheso;

--
-- TOC entry 218 (class 1259 OID 29095)
-- Name: gps_preferences; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.gps_preferences (
    id integer DEFAULT nextval('public.gps_preferences_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_user integer NOT NULL,
    gps_integrertraduction boolean DEFAULT true,
    gps_reemplacertraduction boolean DEFAULT true,
    gps_alignementautomatique boolean DEFAULT true,
    id_alignement_source integer NOT NULL
);


ALTER TABLE public.gps_preferences OWNER TO opentheso;

--
-- TOC entry 219 (class 1259 OID 29105)
-- Name: hierarchical_relationship; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.hierarchical_relationship (
    id_concept1 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    role character varying NOT NULL,
    id_concept2 character varying NOT NULL
);


ALTER TABLE public.hierarchical_relationship OWNER TO opentheso;

--
-- TOC entry 220 (class 1259 OID 29111)
-- Name: hierarchical_relationship_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.hierarchical_relationship_historique (
    id_concept1 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    role character varying NOT NULL,
    id_concept2 character varying NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL,
    action character varying NOT NULL
);


ALTER TABLE public.hierarchical_relationship_historique OWNER TO opentheso;

--
-- TOC entry 221 (class 1259 OID 29118)
-- Name: images; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.images (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    image_name character varying NOT NULL,
    image_copyright character varying NOT NULL,
    id_user integer
);


ALTER TABLE public.images OWNER TO opentheso;

--
-- TOC entry 222 (class 1259 OID 29124)
-- Name: info; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.info (
    version_opentheso character varying NOT NULL,
    version_bdd character varying NOT NULL
);


ALTER TABLE public.info OWNER TO opentheso;

--
-- TOC entry 223 (class 1259 OID 29130)
-- Name: languages_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.languages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.languages_id_seq OWNER TO opentheso;

--
-- TOC entry 265 (class 1259 OID 108999)
-- Name: languages_iso639; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.languages_iso639 (
    iso639_1 character(3),
    iso639_2 character varying,
    english_name character varying,
    french_name character varying,
    id integer DEFAULT nextval('public.languages_id_seq'::regclass) NOT NULL
);


ALTER TABLE public.languages_iso639 OWNER TO opentheso;

--
-- TOC entry 224 (class 1259 OID 29139)
-- Name: node_label; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.node_label (
    facet_id integer NOT NULL,
    id_thesaurus character varying NOT NULL,
    lexical_value character varying,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    lang character varying NOT NULL
);


ALTER TABLE public.node_label OWNER TO opentheso;

--
-- TOC entry 225 (class 1259 OID 29147)
-- Name: non_preferred_term; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.non_preferred_term (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying,
    hiden boolean DEFAULT false NOT NULL
);


ALTER TABLE public.non_preferred_term OWNER TO opentheso;

--
-- TOC entry 226 (class 1259 OID 29156)
-- Name: non_preferred_term_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.non_preferred_term_historique (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying,
    hiden boolean DEFAULT false NOT NULL,
    id_user integer NOT NULL,
    action character varying NOT NULL
);


ALTER TABLE public.non_preferred_term_historique OWNER TO opentheso;

--
-- TOC entry 227 (class 1259 OID 29164)
-- Name: note__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.note__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.note__id_seq OWNER TO opentheso;

--
-- TOC entry 228 (class 1259 OID 29166)
-- Name: note; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.note (
    id integer DEFAULT nextval('public.note__id_seq'::regclass) NOT NULL,
    notetypecode text NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_term character varying,
    id_concept character varying,
    lang character varying NOT NULL,
    lexicalvalue character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.note OWNER TO opentheso;

--
-- TOC entry 229 (class 1259 OID 29175)
-- Name: note_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.note_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.note_historique__id_seq OWNER TO opentheso;

--
-- TOC entry 230 (class 1259 OID 29177)
-- Name: note_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.note_historique (
    id integer DEFAULT nextval('public.note_historique__id_seq'::regclass) NOT NULL,
    notetypecode text NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_term character varying,
    id_concept character varying,
    lang character varying NOT NULL,
    lexicalvalue character varying NOT NULL,
    modified timestamp(6) without time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.note_historique OWNER TO opentheso;

--
-- TOC entry 231 (class 1259 OID 29185)
-- Name: note_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.note_type (
    code text NOT NULL,
    isterm boolean NOT NULL,
    isconcept boolean NOT NULL,
    CONSTRAINT chk_not_false_values CHECK ((NOT ((isterm = false) AND (isconcept = false))))
);


ALTER TABLE public.note_type OWNER TO opentheso;

--
-- TOC entry 232 (class 1259 OID 29192)
-- Name: permuted; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.permuted (
    ord integer NOT NULL,
    id_concept character varying NOT NULL,
    id_group character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_lang character varying NOT NULL,
    lexical_value character varying NOT NULL,
    ispreferredterm boolean NOT NULL,
    original_value character varying
);


ALTER TABLE public.permuted OWNER TO opentheso;

--
-- TOC entry 233 (class 1259 OID 29198)
-- Name: pref__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.pref__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.pref__id_seq OWNER TO opentheso;

--
-- TOC entry 234 (class 1259 OID 29200)
-- Name: preferences; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.preferences (
    id_pref integer DEFAULT nextval('public.pref__id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    source_lang character varying(2) DEFAULT 'fr'::character varying,
    identifier_type integer DEFAULT 2,
    path_image character varying DEFAULT '/var/www/images/'::character varying,
    dossier_resize character varying DEFAULT 'resize'::character varying,
    bdd_active boolean DEFAULT false,
    bdd_use_id boolean DEFAULT false,
    url_bdd character varying DEFAULT 'http://www.mondomaine.fr/concept/##value##'::character varying,
    url_counter_bdd character varying DEFAULT 'http://mondomaine.fr/concept/##conceptId##/total'::character varying,
    z3950actif boolean DEFAULT false,
    collection_adresse character varying DEFAULT 'KOHA/biblios'::character varying,
    notice_url character varying DEFAULT 'http://catalogue.mondomaine.fr/cgi-bin/koha/opac-search.pl?type=opac&op=do_search&q=an=terme'::character varying,
    url_encode character varying(10) DEFAULT 'UTF-8'::character varying,
    path_notice1 character varying DEFAULT '/var/www/notices/repositories.xml'::character varying,
    path_notice2 character varying DEFAULT '/var/www/notices/SchemaMappings.xml'::character varying,
    chemin_site character varying DEFAULT 'http://mondomaine.fr/'::character varying,
    webservices boolean DEFAULT true,
    use_ark boolean DEFAULT false,
    server_ark character varying DEFAULT 'http://ark.mondomaine.fr/ark:/'::character varying,
    id_naan character varying DEFAULT '66666'::character varying NOT NULL,
    prefix_ark character varying DEFAULT 'crt'::character varying NOT NULL,
    user_ark character varying,
    pass_ark character varying,
    use_handle boolean DEFAULT false,
    user_handle character varying,
    pass_handle character varying,
    path_key_handle character varying DEFAULT '/certificat/key.p12'::character varying,
    path_cert_handle character varying DEFAULT '/certificat/cacerts2'::character varying,
    url_api_handle character varying DEFAULT 'https://handle-server.mondomaine.fr:8001/api/handles/'::character varying NOT NULL,
    prefix_handle character varying DEFAULT '66.666.66666'::character varying NOT NULL,
    private_prefix_handle character varying DEFAULT 'crt'::character varying NOT NULL
);


ALTER TABLE public.preferences OWNER TO opentheso;

--
-- TOC entry 235 (class 1259 OID 29233)
-- Name: preferences_sparql; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.preferences_sparql (
    adresse_serveur character varying,
    mot_de_passe character varying,
    nom_d_utilisateur character varying,
    graph character varying,
    synchronisation boolean DEFAULT false NOT NULL,
    thesaurus character varying NOT NULL,
    heure time without time zone
);


ALTER TABLE public.preferences_sparql OWNER TO opentheso;

--
-- TOC entry 236 (class 1259 OID 29240)
-- Name: preferred_term; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.preferred_term (
    id_concept character varying NOT NULL,
    id_term character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.preferred_term OWNER TO opentheso;

--
-- TOC entry 237 (class 1259 OID 29246)
-- Name: proposition; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.proposition (
    id_concept character varying NOT NULL,
    id_user integer NOT NULL,
    id_thesaurus character varying NOT NULL,
    note text,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    concept_parent character varying,
    id_group character varying
);


ALTER TABLE public.proposition OWNER TO opentheso;

--
-- TOC entry 238 (class 1259 OID 29254)
-- Name: relation_group; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.relation_group (
    id_group1 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    relation character varying NOT NULL,
    id_group2 character varying NOT NULL
);


ALTER TABLE public.relation_group OWNER TO opentheso;

--
-- TOC entry 239 (class 1259 OID 29260)
-- Name: roles; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.roles (
    id integer NOT NULL,
    name character varying,
    description character varying
);


ALTER TABLE public.roles OWNER TO opentheso;

--
-- TOC entry 240 (class 1259 OID 29266)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO opentheso;

--
-- TOC entry 3079 (class 0 OID 0)
-- Dependencies: 240
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: opentheso
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.roles.id;


--
-- TOC entry 241 (class 1259 OID 29268)
-- Name: routine_mail; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.routine_mail (
    id_thesaurus character varying NOT NULL,
    alert_cdt boolean DEFAULT true,
    debut_env_cdt_propos date NOT NULL,
    debut_env_cdt_valid date NOT NULL,
    period_env_cdt_propos integer NOT NULL,
    period_env_cdt_valid integer NOT NULL
);


ALTER TABLE public.routine_mail OWNER TO opentheso;

--
-- TOC entry 242 (class 1259 OID 29275)
-- Name: split_non_preferred_term; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.split_non_preferred_term (
);


ALTER TABLE public.split_non_preferred_term OWNER TO opentheso;

--
-- TOC entry 243 (class 1259 OID 29278)
-- Name: term__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.term__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.term__id_seq OWNER TO opentheso;

--
-- TOC entry 244 (class 1259 OID 29280)
-- Name: term; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.term (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying DEFAULT 'D'::character varying,
    id integer DEFAULT nextval('public.term__id_seq'::regclass) NOT NULL,
    contributor integer,
    creator integer
);


ALTER TABLE public.term OWNER TO opentheso;

--
-- TOC entry 245 (class 1259 OID 29290)
-- Name: term_candidat__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.term_candidat__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.term_candidat__id_seq OWNER TO opentheso;

--
-- TOC entry 246 (class 1259 OID 29292)
-- Name: term_candidat; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.term_candidat (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    contributor integer NOT NULL,
    id integer DEFAULT nextval('public.term_candidat__id_seq'::regclass) NOT NULL
);


ALTER TABLE public.term_candidat OWNER TO opentheso;

--
-- TOC entry 247 (class 1259 OID 29301)
-- Name: term_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.term_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.term_historique__id_seq OWNER TO opentheso;

--
-- TOC entry 248 (class 1259 OID 29303)
-- Name: term_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.term_historique (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying DEFAULT 'D'::character varying,
    id integer DEFAULT nextval('public.term_historique__id_seq'::regclass) NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.term_historique OWNER TO opentheso;

--
-- TOC entry 249 (class 1259 OID 29312)
-- Name: thesaurus_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.thesaurus_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.thesaurus_id_seq OWNER TO opentheso;

--
-- TOC entry 250 (class 1259 OID 29314)
-- Name: thesaurus; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.thesaurus (
    id_thesaurus character varying NOT NULL,
    id_ark character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    id integer DEFAULT nextval('public.thesaurus_id_seq'::regclass) NOT NULL,
    private boolean DEFAULT false
);


ALTER TABLE public.thesaurus OWNER TO opentheso;

--
-- TOC entry 251 (class 1259 OID 29324)
-- Name: thesaurus_alignement_source; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.thesaurus_alignement_source (
    id_thesaurus character varying NOT NULL,
    id_alignement_source integer NOT NULL
);


ALTER TABLE public.thesaurus_alignement_source OWNER TO opentheso;

--
-- TOC entry 252 (class 1259 OID 29330)
-- Name: thesaurus_array; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.thesaurus_array (
    facet_id integer DEFAULT nextval('public.facet_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_concept_parent character varying NOT NULL,
    ordered boolean DEFAULT false NOT NULL,
    notation character varying
);


ALTER TABLE public.thesaurus_array OWNER TO opentheso;

--
-- TOC entry 253 (class 1259 OID 29338)
-- Name: thesaurus_array_concept; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.thesaurus_array_concept (
    thesaurusarrayid integer NOT NULL,
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    arrayorder integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.thesaurus_array_concept OWNER TO opentheso;

--
-- TOC entry 254 (class 1259 OID 29345)
-- Name: thesaurus_label; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.thesaurus_label (
    id_thesaurus character varying NOT NULL,
    contributor character varying,
    coverage character varying,
    creator character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    description character varying,
    format character varying,
    lang character varying NOT NULL,
    publisher character varying,
    relation character varying,
    rights character varying,
    source character varying,
    subject character varying,
    title character varying NOT NULL,
    type character varying
);


ALTER TABLE public.thesaurus_label OWNER TO opentheso;

--
-- TOC entry 255 (class 1259 OID 29353)
-- Name: user__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.user__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user__id_seq OWNER TO opentheso;

--
-- TOC entry 263 (class 1259 OID 63858)
-- Name: user_group_label__id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE public.user_group_label__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_group_label__id_seq OWNER TO opentheso;

--
-- TOC entry 261 (class 1259 OID 46260)
-- Name: user_group_label; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_group_label (
    id_group integer DEFAULT nextval('public.user_group_label__id_seq'::regclass) NOT NULL,
    label_group character varying
);


ALTER TABLE public.user_group_label OWNER TO opentheso;

--
-- TOC entry 260 (class 1259 OID 46232)
-- Name: user_group_thesaurus; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_group_thesaurus (
    id_group integer NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.user_group_thesaurus OWNER TO opentheso;

--
-- TOC entry 262 (class 1259 OID 46290)
-- Name: user_role_group; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_role_group (
    id_user integer NOT NULL,
    id_role integer NOT NULL,
    id_group integer NOT NULL
);


ALTER TABLE public.user_role_group OWNER TO opentheso;

--
-- TOC entry 264 (class 1259 OID 64523)
-- Name: user_role_only_on; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_role_only_on (
    id_user integer NOT NULL,
    id_role integer NOT NULL,
    id_theso character varying NOT NULL,
    id_theso_domain character varying DEFAULT 'all'::character varying NOT NULL
);


ALTER TABLE public.user_role_only_on OWNER TO opentheso;

--
-- TOC entry 256 (class 1259 OID 29361)
-- Name: users; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.users (
    id_user integer DEFAULT nextval('public.user__id_seq'::regclass) NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    active boolean DEFAULT true NOT NULL,
    mail character varying NOT NULL,
    passtomodify boolean DEFAULT false,
    alertmail boolean DEFAULT false,
    issuperadmin boolean DEFAULT false
);


ALTER TABLE public.users OWNER TO opentheso;

--
-- TOC entry 257 (class 1259 OID 29370)
-- Name: users2; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.users2 (
    id_user integer DEFAULT nextval('public.user__id_seq'::regclass) NOT NULL,
    login character varying NOT NULL,
    fullname character varying,
    password character varying,
    active boolean DEFAULT true NOT NULL,
    mail character varying,
    authentication public.auth_method DEFAULT 'DB'::public.auth_method
);


ALTER TABLE public.users2 OWNER TO opentheso;

--
-- TOC entry 258 (class 1259 OID 29379)
-- Name: users_historique; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.users_historique (
    id_user integer NOT NULL,
    username character varying,
    created timestamp(6) with time zone DEFAULT now() NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    delete timestamp(6) with time zone
);


ALTER TABLE public.users_historique OWNER TO opentheso;

--
-- TOC entry 259 (class 1259 OID 29387)
-- Name: version_history; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.version_history (
    "idVersionhistory" character varying NOT NULL,
    "idThesaurus" character varying NOT NULL,
    date date,
    "versionNote" character varying,
    "currentVersion" boolean,
    "thisVersion" boolean NOT NULL
);


ALTER TABLE public.version_history OWNER TO opentheso;

--
-- TOC entry 2686 (class 2604 OID 109096)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- TOC entry 2991 (class 0 OID 28917)
-- Dependencies: 186
-- Data for Name: alignement; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 2993 (class 0 OID 28928)
-- Dependencies: 188
-- Data for Name: alignement_preferences; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 2995 (class 0 OID 28937)
-- Dependencies: 190
-- Data for Name: alignement_source; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Geoname', 'http://api.geonames.org/search?q=##value##&maxRows=10&style=FULL&lang=##lang##&username=opentheso', 'REST', 'xml', 1, NULL, 'test de geonames', true);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Pactols', 'http://pactols.frantiq.fr/opentheso/webresources/rest/skos/concept/value=##value##&lang=##lang##&th=TH_1', 'REST', 'skos', 2, NULL, '', false);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('wikidata', 'SELECT ?item ?itemLabel ?itemDescription WHERE {
            ?item rdfs:label "##value##"@##lang##.
            SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],##lang##". }
}', 'SPARQL', 'json', 3, 9, 'alignement avec le thésaurus de wikidata', false);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('bnf_instrumentMusique', 'PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX xml: <http://www.w3.org/XML/1998/namespace>
SELECT ?instrument ?prop ?value where {
  <http://data.bnf.fr/ark:/12148/cb119367821> skos:narrower+ ?instrument.
  ?instrument ?prop ?value.
  FILTER( (regex(?prop,skos:prefLabel) || regex(?prop,skos:altLabel))  && regex(?value, ##value##,"i") ) 
    filter(lang(?value) =##lang##)
} LIMIT 20', 'SPARQL', 'skos', 4, NULL, '', false);

INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Gemet', 'http://www.eionet.europa.eu/gemet/getConceptsMatchingKeyword?keyword=##value##&search_mode=3&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/&language=##lang##', 'REST', 'json', 5, NULL, 'source Gemet', false);


--
-- TOC entry 2996 (class 0 OID 28945)
-- Dependencies: 191
-- Data for Name: alignement_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (1, 'Equivalence exacte', '=EQ', 'exactMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (2, 'Equivalence inexacte', '~EQ', 'closeMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (3, 'Equivalence générique', 'EQB', 'broadMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (4, 'Equivalence associative', 'EQR', 'relatedMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (5, 'Equivalence spécifique', 'EQS', 'narrowMatch');


--
-- TOC entry 2997 (class 0 OID 28951)
-- Dependencies: 192
-- Data for Name: compound_equivalence; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 2999 (class 0 OID 28959)
-- Dependencies: 194
-- Data for Name: concept; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3001 (class 0 OID 28974)
-- Dependencies: 196
-- Data for Name: concept_candidat; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3002 (class 0 OID 28984)
-- Dependencies: 197
-- Data for Name: concept_fusion; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3004 (class 0 OID 28993)
-- Dependencies: 199
-- Data for Name: concept_group; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3005 (class 0 OID 29001)
-- Dependencies: 200
-- Data for Name: concept_group_concept; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3007 (class 0 OID 29009)
-- Dependencies: 202
-- Data for Name: concept_group_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3009 (class 0 OID 29019)
-- Dependencies: 204
-- Data for Name: concept_group_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3011 (class 0 OID 29030)
-- Dependencies: 206
-- Data for Name: concept_group_label_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3012 (class 0 OID 29038)
-- Dependencies: 207
-- Data for Name: concept_group_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('MT', 'Microthesaurus', 'MicroThesaurus');
INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('G', 'Group', 'ConceptGroup');
INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('C', 'Collection', 'Collection');
INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('T', 'Theme', 'Theme');


--
-- TOC entry 3014 (class 0 OID 29046)
-- Dependencies: 209
-- Data for Name: concept_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3015 (class 0 OID 29055)
-- Dependencies: 210
-- Data for Name: concept_orphan; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3016 (class 0 OID 29061)
-- Dependencies: 211
-- Data for Name: concept_term_candidat; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3017 (class 0 OID 29067)
-- Dependencies: 212
-- Data for Name: copyright; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3018 (class 0 OID 29073)
-- Dependencies: 213
-- Data for Name: custom_concept_attribute; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3019 (class 0 OID 29079)
-- Dependencies: 214
-- Data for Name: custom_term_attribute; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3021 (class 0 OID 29087)
-- Dependencies: 216
-- Data for Name: gps; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3023 (class 0 OID 29095)
-- Dependencies: 218
-- Data for Name: gps_preferences; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3024 (class 0 OID 29105)
-- Dependencies: 219
-- Data for Name: hierarchical_relationship; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3025 (class 0 OID 29111)
-- Dependencies: 220
-- Data for Name: hierarchical_relationship_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3026 (class 0 OID 29118)
-- Dependencies: 221
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3027 (class 0 OID 29124)
-- Dependencies: 222
-- Data for Name: info; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3070 (class 0 OID 108999)
-- Dependencies: 265
-- Data for Name: languages_iso639; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('aa ', 'aar', 'Afar', 'afar', 2);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ab ', 'abk', 'Abkhazian', 'abkhaze', 3);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('af ', 'afr', 'Afrikaans', 'afrikaans', 4);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ak ', 'aka', 'Akan', 'akan', 5);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sq ', 'alb (B)
sqi (T)', 'Albanian', 'albanais', 6);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('am ', 'amh', 'Amharic', 'amharique', 7);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ar ', 'ara', 'Arabic', 'arabe', 8);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('an ', 'arg', 'Aragonese', 'aragonais', 9);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('as ', 'asm', 'Assamese', 'assamais', 10);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('av ', 'ava', 'Avaric', 'avar', 11);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ae ', 'ave', 'Avestan', 'avestique', 12);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ay ', 'aym', 'Aymara', 'aymara', 13);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('az ', 'aze', 'Azerbaijani', 'azéri', 14);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ba ', 'bak', 'Bashkir', 'bachkir', 15);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bm ', 'bam', 'Bambara', 'bambara', 16);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('eu ', 'baq (B)
eus (T)', 'Basque', 'basque', 17);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('be ', 'bel', 'Belarusian', 'biélorusse', 18);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bn ', 'ben', 'Bengali', 'bengali', 19);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bh ', 'bih', 'Bihari languages', 'langues biharis', 20);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bi ', 'bis', 'Bislama', 'bichlamar', 21);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bs ', 'bos', 'Bosnian', 'bosniaque', 22);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('br ', 'bre', 'Breton', 'breton', 23);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bg ', 'bul', 'Bulgarian', 'bulgare', 24);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ca ', 'cat', 'Catalan; Valencian', 'catalan; valencien', 25);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ch ', 'cha', 'Chamorro', 'chamorro', 26);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ce ', 'che', 'Chechen', 'tchétchène', 27);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('zh ', 'chi (B)
zho (T)', 'Chinese', 'chinois', 28);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cv ', 'chv', 'Chuvash', 'tchouvache', 29);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kw ', 'cor', 'Cornish', 'cornique', 30);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('co ', 'cos', 'Corsican', 'corse', 31);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cr ', 'cre', 'Cree', 'cree', 32);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cy ', 'wel (B)
cym (T)', 'Welsh', 'gallois', 33);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cs ', 'cze (B)
ces (T)', 'Czech', 'tchèque', 34);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('da ', 'dan', 'Danish', 'danois', 35);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('de ', 'ger (B)
deu (T)', 'German', 'allemand', 36);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('dv ', 'div', 'Divehi; Dhivehi; Maldivian', 'maldivien', 37);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('dz ', 'dzo', 'Dzongkha', 'dzongkha', 38);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('el ', 'gre (B)
ell (T)', 'Greek, Modern (1453-)', 'grec moderne (après 1453)', 39);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('en ', 'eng', 'English', 'anglais', 40);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('eo ', 'epo', 'Esperanto', 'espéranto', 41);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('et ', 'est', 'Estonian', 'estonien', 42);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ee ', 'ewe', 'Ewe', 'éwé', 43);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fo ', 'fao', 'Faroese', 'féroïen', 44);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fj ', 'fij', 'Fijian', 'fidjien', 45);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fi ', 'fin', 'Finnish', 'finnois', 46);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fr ', 'fre (B)
fra (T)', 'French', 'français', 47);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fy ', 'fry', 'Western Frisian', 'frison occidental', 48);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ff ', 'ful', 'Fulah', 'peul', 49);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ka ', 'geo (B)
kat (T)', 'Georgian', 'géorgien', 50);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gd ', 'gla', 'Gaelic; Scottish Gaelic', 'gaélique; gaélique écossais', 51);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ga ', 'gle', 'Irish', 'irlandais', 52);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gl ', 'glg', 'Galician', 'galicien', 53);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gv ', 'glv', 'Manx', 'manx; mannois', 54);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gn ', 'grn', 'Guarani', 'guarani', 55);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gu ', 'guj', 'Gujarati', 'goudjrati', 56);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ht ', 'hat', 'Haitian; Haitian Creole', 'haïtien; créole haïtien', 57);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ha ', 'hau', 'Hausa', 'haoussa', 58);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('he ', 'heb', 'Hebrew', 'hébreu', 59);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hz ', 'her', 'Herero', 'herero', 60);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hi ', 'hin', 'Hindi', 'hindi', 61);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ho ', 'hmo', 'Hiri Motu', 'hiri motu', 62);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hr ', 'hrv', 'Croatian', 'croate', 63);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hu ', 'hun', 'Hungarian', 'hongrois', 64);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hy ', 'arm (B)
hye (T)', 'Armenian', 'arménien', 65);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ig ', 'ibo', 'Igbo', 'igbo', 66);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('is ', 'ice (B)
isl (T)', 'Icelandic', 'islandais', 67);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('io ', 'ido', 'Ido', 'ido', 68);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ii ', 'iii', 'Sichuan Yi; Nuosu', 'yi de Sichuan', 69);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('iu ', 'iku', 'Inuktitut', 'inuktitut', 70);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ie ', 'ile', 'Interlingue; Occidental', 'interlingue', 71);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('id ', 'ind', 'Indonesian', 'indonésien', 72);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ik ', 'ipk', 'Inupiaq', 'inupiaq', 73);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('it ', 'ita', 'Italian', 'italien', 74);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('jv ', 'jav', 'Javanese', 'javanais', 75);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ja ', 'jpn', 'Japanese', 'japonais', 76);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kl ', 'kal', 'Kalaallisut; Greenlandic', 'groenlandais', 77);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kn ', 'kan', 'Kannada', 'kannada', 78);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ks ', 'kas', 'Kashmiri', 'kashmiri', 79);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kr ', 'kau', 'Kanuri', 'kanouri', 80);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kk ', 'kaz', 'Kazakh', 'kazakh', 81);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('km ', 'khm', 'Central Khmer', 'khmer central', 82);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ki ', 'kik', 'Kikuyu; Gikuyu', 'kikuyu', 83);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('rw ', 'kin', 'Kinyarwanda', 'rwanda', 84);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ky ', 'kir', 'Kirghiz; Kyrgyz', 'kirghiz', 85);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kv ', 'kom', 'Komi', 'kom', 86);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kg ', 'kon', 'Kongo', 'kongo', 87);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ko ', 'kor', 'Korean', 'coréen', 88);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kj ', 'kua', 'Kuanyama; Kwanyama', 'kuanyama; kwanyama', 89);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ku ', 'kur', 'Kurdish', 'kurde', 90);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lo ', 'lao', 'Lao', 'lao', 91);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('la ', 'lat', 'Latin', 'latin', 92);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lv ', 'lav', 'Latvian', 'letton', 93);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('li ', 'lim', 'Limburgan; Limburger; Limburgish', 'limbourgeois', 94);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ln ', 'lin', 'Lingala', 'lingala', 95);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lt ', 'lit', 'Lithuanian', 'lituanien', 96);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lb ', 'ltz', 'Luxembourgish; Letzeburgesch', 'luxembourgeois', 97);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lu ', 'lub', 'Luba-Katanga', 'luba-katanga', 98);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lg ', 'lug', 'Ganda', 'ganda', 99);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mk ', 'mac (B)
mkd (T)', 'Macedonian', 'macédonien', 100);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mh ', 'mah', 'Marshallese', 'marshall', 101);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ml ', 'mal', 'Malayalam', 'malayalam', 102);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mr ', 'mar', 'Marathi', 'marathe', 103);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ms ', 'may (B)
msa (T)', 'Malay', 'malais', 104);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mg ', 'mlg', 'Malagasy', 'malgache', 105);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mt ', 'mlt', 'Maltese', 'maltais', 106);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mn ', 'mon', 'Mongolian', 'mongol', 107);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mi ', 'mao (B)
mri (T)', 'Maori', 'maori', 108);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('my ', 'bur (B)
mya (T)', 'Burmese', 'birman', 109);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('na ', 'nau', 'Nauru', 'nauruan', 110);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nv ', 'nav', 'Navajo; Navaho', 'navaho', 111);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nr ', 'nbl', 'Ndebele, South; South Ndebele', 'ndébélé du Sud', 112);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nd ', 'nde', 'Ndebele, North; North Ndebele', 'ndébélé du Nord', 113);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ng ', 'ndo', 'Ndonga', 'ndonga', 114);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ne ', 'nep', 'Nepali', 'népalais', 115);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nl ', 'dut (B)
nld (T)', 'Dutch; Flemish', 'néerlandais; flamand', 116);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nb ', 'nob', 'Bokmål, Norwegian; Norwegian Bokmål', 'norvégien bokmål', 117);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('no ', 'nor', 'Norwegian', 'norvégien', 118);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ny ', 'nya', 'Chichewa; Chewa; Nyanja', 'chichewa; chewa; nyanja', 119);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('oc ', 'oci', 'Occitan (post 1500)', 'occitan (après 1500)', 120);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('oj ', 'oji', 'Ojibwa', 'ojibwa', 121);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('or ', 'ori', 'Oriya', 'oriya', 122);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('om ', 'orm', 'Oromo', 'galla', 123);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('os ', 'oss', 'Ossetian; Ossetic', 'ossète', 124);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pa ', 'pan', 'Panjabi; Punjabi', 'pendjabi', 125);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fa ', 'per (B)
fas (T)', 'Persian', 'persan', 126);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pi ', 'pli', 'Pali', 'pali', 127);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pl ', 'pol', 'Polish', 'polonais', 128);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pt ', 'por', 'Portuguese', 'portugais', 129);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ps ', 'pus', 'Pushto; Pashto', 'pachto', 130);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('qu ', 'que', 'Quechua', 'quechua', 131);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('rm ', 'roh', 'Romansh', 'romanche', 132);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ro ', 'rum (B)
ron (T)', 'Romanian; Moldavian; Moldovan', 'roumain; moldave', 133);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('rn ', 'run', 'Rundi', 'rundi', 134);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ru ', 'rus', 'Russian', 'russe', 135);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sg ', 'sag', 'Sango', 'sango', 136);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sa ', 'san', 'Sanskrit', 'sanskrit', 137);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('si ', 'sin', 'Sinhala; Sinhalese', 'singhalais', 138);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sk ', 'slo (B)
slk (T)', 'Slovak', 'slovaque', 139);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sl ', 'slv', 'Slovenian', 'slovène', 140);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('se ', 'sme', 'Northern Sami', 'sami du Nord', 141);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sm ', 'smo', 'Samoan', 'samoan', 142);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sn ', 'sna', 'Shona', 'shona', 143);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sd ', 'snd', 'Sindhi', 'sindhi', 144);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('so ', 'som', 'Somali', 'somali', 145);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('st ', 'sot', 'Sotho, Southern', 'sotho du Sud', 146);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('es ', 'spa', 'Spanish; Castilian', 'espagnol; castillan', 147);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sc ', 'srd', 'Sardinian', 'sarde', 148);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sr ', 'srp', 'Serbian', 'serbe', 149);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ss ', 'ssw', 'Swati', 'swati', 150);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('su ', 'sun', 'Sundanese', 'soundanais', 151);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sw ', 'swa', 'Swahili', 'swahili', 152);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sv ', 'swe', 'Swedish', 'suédois', 153);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ty ', 'tah', 'Tahitian', 'tahitien', 154);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ta ', 'tam', 'Tamil', 'tamoul', 155);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tt ', 'tat', 'Tatar', 'tatar', 156);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('te ', 'tel', 'Telugu', 'télougou', 157);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tg ', 'tgk', 'Tajik', 'tadjik', 158);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tl ', 'tgl', 'Tagalog', 'tagalog', 159);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('th ', 'tha', 'Thai', 'thaï', 160);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bo ', 'tib (B)
bod (T)', 'Tibetan', 'tibétain', 161);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ti ', 'tir', 'Tigrinya', 'tigrigna', 162);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('to ', 'ton', 'Tonga (Tonga Islands)', 'tongan (Îles Tonga)', 163);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tn ', 'tsn', 'Tswana', 'tswana', 164);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ts ', 'tso', 'Tsonga', 'tsonga', 165);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tk ', 'tuk', 'Turkmen', 'turkmène', 166);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tr ', 'tur', 'Turkish', 'turc', 167);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tw ', 'twi', 'Twi', 'twi', 168);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ug ', 'uig', 'Uighur; Uyghur', 'ouïgour', 169);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('uk ', 'ukr', 'Ukrainian', 'ukrainien', 170);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ur ', 'urd', 'Urdu', 'ourdou', 171);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('uz ', 'uzb', 'Uzbek', 'ouszbek', 172);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ve ', 'ven', 'Venda', 'venda', 173);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('vi ', 'vie', 'Vietnamese', 'vietnamien', 174);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('vo ', 'vol', 'Volapük', 'volapük', 175);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('wa ', 'wln', 'Walloon', 'wallon', 176);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('wo ', 'wol', 'Wolof', 'wolof', 177);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('xh ', 'xho', 'Xhosa', 'xhosa', 178);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('yi ', 'yid', 'Yiddish', 'yiddish', 179);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('yo ', 'yor', 'Yoruba', 'yoruba', 180);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('za ', 'zha', 'Zhuang; Chuang', 'zhuang; chuang', 181);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('zu ', 'zul', 'Zulu', 'zoulou', 182);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cu ', 'chu', 'Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic', 'vieux slave; vieux bulgare', 183);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ia ', 'ina', 'Interlingua (International Auxiliary Language Association)', 'interlingua', 184);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nn ', 'nno', 'Norwegian Nynorsk; Nynorsk, Norwegian', 'norvégien nynorsk', 185);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gr ', 'grc', 'Greek, Ancient (to 1453)', 'grec ancien (jusqu''à 1453)', 186);


--
-- TOC entry 3029 (class 0 OID 29139)
-- Dependencies: 224
-- Data for Name: node_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3030 (class 0 OID 29147)
-- Dependencies: 225
-- Data for Name: non_preferred_term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3031 (class 0 OID 29156)
-- Dependencies: 226
-- Data for Name: non_preferred_term_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3033 (class 0 OID 29166)
-- Dependencies: 228
-- Data for Name: note; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3035 (class 0 OID 29177)
-- Dependencies: 230
-- Data for Name: note_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3036 (class 0 OID 29185)
-- Dependencies: 231
-- Data for Name: note_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('customNote', false, true);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('definition', true, false);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('editorialNote', true, false);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('historyNote', true, true);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('scopeNote', false, true);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('note', false, true);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('example', true, false);
INSERT INTO public.note_type (code, isterm, isconcept) VALUES ('changeNote', true, false);


--
-- TOC entry 3037 (class 0 OID 29192)
-- Dependencies: 232
-- Data for Name: permuted; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3039 (class 0 OID 29200)
-- Dependencies: 234
-- Data for Name: preferences; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3040 (class 0 OID 29233)
-- Dependencies: 235
-- Data for Name: preferences_sparql; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3041 (class 0 OID 29240)
-- Dependencies: 236
-- Data for Name: preferred_term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3042 (class 0 OID 29246)
-- Dependencies: 237
-- Data for Name: proposition; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3043 (class 0 OID 29254)
-- Dependencies: 238
-- Data for Name: relation_group; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3044 (class 0 OID 29260)
-- Dependencies: 239
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.roles (id, name, description) VALUES (1, 'superAdmin', 'Super Administrateur pour tout gérer tout thésaurus et tout utilisateur');
INSERT INTO public.roles (id, name, description) VALUES (2, 'admin', 'administrateur pour un domaine ou parc de thésaurus');
INSERT INTO public.roles (id, name, description) VALUES (3, 'manager', 'gestionnaire de thésaurus, pas de création de thésaurus');
INSERT INTO public.roles (id, name, description) VALUES (4, 'contributor', 'traducteur, notes, candidats, images');


--
-- TOC entry 3046 (class 0 OID 29268)
-- Dependencies: 241
-- Data for Name: routine_mail; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3047 (class 0 OID 29275)
-- Dependencies: 242
-- Data for Name: split_non_preferred_term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3049 (class 0 OID 29280)
-- Dependencies: 244
-- Data for Name: term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3051 (class 0 OID 29292)
-- Dependencies: 246
-- Data for Name: term_candidat; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3053 (class 0 OID 29303)
-- Dependencies: 248
-- Data for Name: term_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3055 (class 0 OID 29314)
-- Dependencies: 250
-- Data for Name: thesaurus; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3056 (class 0 OID 29324)
-- Dependencies: 251
-- Data for Name: thesaurus_alignement_source; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3057 (class 0 OID 29330)
-- Dependencies: 252
-- Data for Name: thesaurus_array; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3058 (class 0 OID 29338)
-- Dependencies: 253
-- Data for Name: thesaurus_array_concept; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3059 (class 0 OID 29345)
-- Dependencies: 254
-- Data for Name: thesaurus_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3066 (class 0 OID 46260)
-- Dependencies: 261
-- Data for Name: user_group_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3065 (class 0 OID 46232)
-- Dependencies: 260
-- Data for Name: user_group_thesaurus; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3067 (class 0 OID 46290)
-- Dependencies: 262
-- Data for Name: user_role_group; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3069 (class 0 OID 64523)
-- Dependencies: 264
-- Data for Name: user_role_only_on; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3061 (class 0 OID 29361)
-- Dependencies: 256
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.users (id_user, username, password, active, mail, passtomodify, alertmail, issuperadmin) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', true, 'admin@domaine.fr', false, false, true);


--
-- TOC entry 3062 (class 0 OID 29370)
-- Dependencies: 257
-- Data for Name: users2; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.users2 (id_user, login, fullname, password, active, mail, authentication) VALUES (8, 'miled', 'Miled Rousset', '', true, 'mie@mile', 'DB');
INSERT INTO public.users2 (id_user, login, fullname, password, active, mail, authentication) VALUES (9, 'toto', 'toeot', '', true, 'mil@mf', 'LDAP');


--
-- TOC entry 3063 (class 0 OID 29379)
-- Dependencies: 258
-- Data for Name: users_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3064 (class 0 OID 29387)
-- Dependencies: 259
-- Data for Name: version_history; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3080 (class 0 OID 0)
-- Dependencies: 185
-- Name: alignement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.alignement_id_seq', 1, false);


--
-- TOC entry 3081 (class 0 OID 0)
-- Dependencies: 187
-- Name: alignement_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.alignement_preferences_id_seq', 1, false);


--
-- TOC entry 3082 (class 0 OID 0)
-- Dependencies: 189
-- Name: alignement_source__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.alignement_source__id_seq', 11, false);


--
-- TOC entry 3083 (class 0 OID 0)
-- Dependencies: 193
-- Name: concept__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept__id_seq', 1, false);


--
-- TOC entry 3084 (class 0 OID 0)
-- Dependencies: 195
-- Name: concept_candidat__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_candidat__id_seq', 1, false);


--
-- TOC entry 3085 (class 0 OID 0)
-- Dependencies: 198
-- Name: concept_group__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group__id_seq', 1, false);


--
-- TOC entry 3086 (class 0 OID 0)
-- Dependencies: 201
-- Name: concept_group_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group_historique__id_seq', 1, false);


--
-- TOC entry 3087 (class 0 OID 0)
-- Dependencies: 205
-- Name: concept_group_label_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group_label_historique__id_seq', 1, false);


--
-- TOC entry 3088 (class 0 OID 0)
-- Dependencies: 203
-- Name: concept_group_label_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group_label_id_seq', 1, false);


--
-- TOC entry 3089 (class 0 OID 0)
-- Dependencies: 208
-- Name: concept_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_historique__id_seq', 1, false);


--
-- TOC entry 3090 (class 0 OID 0)
-- Dependencies: 215
-- Name: facet_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.facet_id_seq', 1, false);


--
-- TOC entry 3091 (class 0 OID 0)
-- Dependencies: 217
-- Name: gps_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.gps_preferences_id_seq', 1, false);


--
-- TOC entry 3092 (class 0 OID 0)
-- Dependencies: 223
-- Name: languages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.languages_id_seq', 186, false);


--
-- TOC entry 3093 (class 0 OID 0)
-- Dependencies: 227
-- Name: note__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.note__id_seq', 1, false);


--
-- TOC entry 3094 (class 0 OID 0)
-- Dependencies: 229
-- Name: note_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.note_historique__id_seq', 1, false);


--
-- TOC entry 3095 (class 0 OID 0)
-- Dependencies: 233
-- Name: pref__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.pref__id_seq', 1, false);


--
-- TOC entry 3096 (class 0 OID 0)
-- Dependencies: 240
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.role_id_seq', 6, true);


--
-- TOC entry 3097 (class 0 OID 0)
-- Dependencies: 243
-- Name: term__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.term__id_seq', 1, false);


--
-- TOC entry 3098 (class 0 OID 0)
-- Dependencies: 245
-- Name: term_candidat__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.term_candidat__id_seq', 1, false);


--
-- TOC entry 3099 (class 0 OID 0)
-- Dependencies: 247
-- Name: term_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.term_historique__id_seq', 1, false);


--
-- TOC entry 3100 (class 0 OID 0)
-- Dependencies: 249
-- Name: thesaurus_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.thesaurus_id_seq', 1, false);


--
-- TOC entry 3101 (class 0 OID 0)
-- Dependencies: 255
-- Name: user__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.user__id_seq', 2, false);


--
-- TOC entry 3102 (class 0 OID 0)
-- Dependencies: 263
-- Name: user_group_label__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.user_group_label__id_seq', 1, false);


--
-- TOC entry 2858 (class 2606 OID 29395)
-- Name: version_history VersionHistory_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.version_history
    ADD CONSTRAINT "VersionHistory_pkey" PRIMARY KEY ("idVersionhistory");


--
-- TOC entry 2721 (class 2606 OID 29397)
-- Name: alignement alignement_internal_id_concept_internal_id_thesaurus_id_ali_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement
    ADD CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_id_ali_key UNIQUE (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type, uri_target);


--
-- TOC entry 2723 (class 2606 OID 38279)
-- Name: alignement alignement_internal_id_concept_internal_id_thesaurus_id_alignem; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement
    ADD CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_id_alignem UNIQUE (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type, uri_target);


--
-- TOC entry 2725 (class 2606 OID 29399)
-- Name: alignement alignement_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement
    ADD CONSTRAINT alignement_pkey PRIMARY KEY (id);


--
-- TOC entry 2727 (class 2606 OID 29401)
-- Name: alignement_preferences alignement_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_preferences
    ADD CONSTRAINT alignement_preferences_pkey PRIMARY KEY (id_thesaurus, id_user, id_concept_depart, id_alignement_source);


--
-- TOC entry 2729 (class 2606 OID 29403)
-- Name: alignement_source alignement_source_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_source
    ADD CONSTRAINT alignement_source_pkey PRIMARY KEY (id);


--
-- TOC entry 2731 (class 2606 OID 29405)
-- Name: alignement_source alignement_source_source_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_source
    ADD CONSTRAINT alignement_source_source_key UNIQUE (source);


--
-- TOC entry 2733 (class 2606 OID 29407)
-- Name: alignement_type alignment_type_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_type
    ADD CONSTRAINT alignment_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2735 (class 2606 OID 29409)
-- Name: compound_equivalence compound_equivalence_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.compound_equivalence
    ADD CONSTRAINT compound_equivalence_pkey PRIMARY KEY (id_split_nonpreferredterm, id_preferredterm);


--
-- TOC entry 2739 (class 2606 OID 29411)
-- Name: concept_candidat concept_candidat_id_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_candidat
    ADD CONSTRAINT concept_candidat_id_key UNIQUE (id);


--
-- TOC entry 2741 (class 2606 OID 29413)
-- Name: concept_candidat concept_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_candidat
    ADD CONSTRAINT concept_candidat_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2761 (class 2606 OID 109095)
-- Name: concept_historique concept_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_historique
    ADD CONSTRAINT concept_copy_pkey PRIMARY KEY (id_concept, id_thesaurus, id_group, id_user, modified);


--
-- TOC entry 2743 (class 2606 OID 29417)
-- Name: concept_fusion concept_fusion_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_fusion
    ADD CONSTRAINT concept_fusion_pkey PRIMARY KEY (id_concept1, id_concept2, id_thesaurus);


--
-- TOC entry 2747 (class 2606 OID 29419)
-- Name: concept_group_concept concept_group_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_concept
    ADD CONSTRAINT concept_group_concept_pkey PRIMARY KEY (idgroup, idthesaurus, idconcept);


--
-- TOC entry 2749 (class 2606 OID 29421)
-- Name: concept_group_historique concept_group_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_historique
    ADD CONSTRAINT concept_group_copy_pkey PRIMARY KEY (idgroup, idthesaurus, modified, id_user);


--
-- TOC entry 2755 (class 2606 OID 29423)
-- Name: concept_group_label_historique concept_group_label_copy_idgrouplabel_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label_historique
    ADD CONSTRAINT concept_group_label_copy_idgrouplabel_key UNIQUE (id);


--
-- TOC entry 2757 (class 2606 OID 29425)
-- Name: concept_group_label_historique concept_group_label_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label_historique
    ADD CONSTRAINT concept_group_label_copy_pkey PRIMARY KEY (lang, idthesaurus, lexicalvalue, modified, id_user);


--
-- TOC entry 2751 (class 2606 OID 29427)
-- Name: concept_group_label concept_group_label_idgrouplabel_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label
    ADD CONSTRAINT concept_group_label_idgrouplabel_key UNIQUE (id);


--
-- TOC entry 2753 (class 2606 OID 29429)
-- Name: concept_group_label concept_group_label_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label
    ADD CONSTRAINT concept_group_label_pkey PRIMARY KEY (lang, idthesaurus, lexicalvalue);


--
-- TOC entry 2745 (class 2606 OID 29431)
-- Name: concept_group concept_group_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group
    ADD CONSTRAINT concept_group_pkey PRIMARY KEY (idgroup, idthesaurus);


--
-- TOC entry 2759 (class 2606 OID 29433)
-- Name: concept_group_type concept_group_type_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_type
    ADD CONSTRAINT concept_group_type_pkey PRIMARY KEY (code, label);


--
-- TOC entry 2763 (class 2606 OID 29435)
-- Name: concept_orphan concept_orphan_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_orphan
    ADD CONSTRAINT concept_orphan_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2737 (class 2606 OID 29437)
-- Name: concept concept_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept
    ADD CONSTRAINT concept_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2765 (class 2606 OID 29439)
-- Name: concept_term_candidat concept_term_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_term_candidat
    ADD CONSTRAINT concept_term_candidat_pkey PRIMARY KEY (id_concept, id_term, id_thesaurus);


--
-- TOC entry 2767 (class 2606 OID 29441)
-- Name: copyright copyright_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.copyright
    ADD CONSTRAINT copyright_pkey PRIMARY KEY (id_thesaurus);


--
-- TOC entry 2769 (class 2606 OID 29443)
-- Name: custom_concept_attribute custom_concept_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.custom_concept_attribute
    ADD CONSTRAINT custom_concept_attribute_pkey PRIMARY KEY ("idConcept");


--
-- TOC entry 2771 (class 2606 OID 29445)
-- Name: custom_term_attribute custom_term_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.custom_term_attribute
    ADD CONSTRAINT custom_term_attribute_pkey PRIMARY KEY (identifier);


--
-- TOC entry 2773 (class 2606 OID 109076)
-- Name: gps gps_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.gps
    ADD CONSTRAINT gps_pkey PRIMARY KEY (id_concept, id_theso);


--
-- TOC entry 2775 (class 2606 OID 29449)
-- Name: gps_preferences gps_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.gps_preferences
    ADD CONSTRAINT gps_preferences_pkey PRIMARY KEY (id_thesaurus, id_user, id_alignement_source);


--
-- TOC entry 2779 (class 2606 OID 29451)
-- Name: hierarchical_relationship_historique hierarchical_relationship_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.hierarchical_relationship_historique
    ADD CONSTRAINT hierarchical_relationship_copy_pkey PRIMARY KEY (id_concept1, id_thesaurus, role, id_concept2, modified, id_user);


--
-- TOC entry 2777 (class 2606 OID 29453)
-- Name: hierarchical_relationship hierarchical_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.hierarchical_relationship
    ADD CONSTRAINT hierarchical_relationship_pkey PRIMARY KEY (id_concept1, id_thesaurus, role, id_concept2);


--
-- TOC entry 2781 (class 2606 OID 29455)
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id_concept, id_thesaurus, image_name);


--
-- TOC entry 2783 (class 2606 OID 29457)
-- Name: info info_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.info
    ADD CONSTRAINT info_pkey PRIMARY KEY (version_opentheso, version_bdd);


--
-- TOC entry 2870 (class 2606 OID 109009)
-- Name: languages_iso639 languages_iso639_iso639_1_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.languages_iso639
    ADD CONSTRAINT languages_iso639_iso639_1_key UNIQUE (iso639_1);


--
-- TOC entry 2872 (class 2606 OID 109007)
-- Name: languages_iso639 languages_iso639_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.languages_iso639
    ADD CONSTRAINT languages_iso639_pkey PRIMARY KEY (id);


--
-- TOC entry 2785 (class 2606 OID 29463)
-- Name: node_label node_label_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.node_label
    ADD CONSTRAINT node_label_pkey PRIMARY KEY (facet_id, id_thesaurus, lang);


--
-- TOC entry 2787 (class 2606 OID 29465)
-- Name: non_preferred_term non_prefered_term_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.non_preferred_term
    ADD CONSTRAINT non_prefered_term_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus);


--
-- TOC entry 2789 (class 2606 OID 29467)
-- Name: non_preferred_term_historique non_preferred_term_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.non_preferred_term_historique
    ADD CONSTRAINT non_preferred_term_copy_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus, modified, id_user);


--
-- TOC entry 2797 (class 2606 OID 29469)
-- Name: note_historique note_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note_historique
    ADD CONSTRAINT note_copy_pkey PRIMARY KEY (id, modified, id_user);


--
-- TOC entry 2791 (class 2606 OID 109072)
-- Name: note note_notetypecode_id_thesaurus_id_concept_lang_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note
    ADD CONSTRAINT note_notetypecode_id_thesaurus_id_concept_lang_key UNIQUE (notetypecode, id_thesaurus, id_concept, lang, lexicalvalue);


--
-- TOC entry 2793 (class 2606 OID 109074)
-- Name: note note_notetypecode_id_thesaurus_id_term_lang_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note
    ADD CONSTRAINT note_notetypecode_id_thesaurus_id_term_lang_key UNIQUE (notetypecode, id_thesaurus, id_term, lang, lexicalvalue);


--
-- TOC entry 2795 (class 2606 OID 29475)
-- Name: note note_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note
    ADD CONSTRAINT note_pkey PRIMARY KEY (id);


--
-- TOC entry 2802 (class 2606 OID 29477)
-- Name: permuted permuted_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.permuted
    ADD CONSTRAINT permuted_pkey PRIMARY KEY (ord, id_concept, id_group, id_thesaurus, id_lang, lexical_value, ispreferredterm);


--
-- TOC entry 2799 (class 2606 OID 29479)
-- Name: note_type pk_note_type; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note_type
    ADD CONSTRAINT pk_note_type PRIMARY KEY (code);


--
-- TOC entry 2814 (class 2606 OID 29481)
-- Name: relation_group pk_relation_group; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.relation_group
    ADD CONSTRAINT pk_relation_group PRIMARY KEY (id_group1, id_thesaurus, relation, id_group2);


--
-- TOC entry 2804 (class 2606 OID 29483)
-- Name: preferences preferences_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT preferences_id_thesaurus_key UNIQUE (id_thesaurus);


--
-- TOC entry 2806 (class 2606 OID 29485)
-- Name: preferences preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT preferences_pkey PRIMARY KEY (id_pref);


--
-- TOC entry 2808 (class 2606 OID 29487)
-- Name: preferences_sparql preferences_sparql_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences_sparql
    ADD CONSTRAINT preferences_sparql_pkey PRIMARY KEY (thesaurus);


--
-- TOC entry 2810 (class 2606 OID 29489)
-- Name: preferred_term preferred_term_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferred_term
    ADD CONSTRAINT preferred_term_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2812 (class 2606 OID 29491)
-- Name: proposition proposition_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.proposition
    ADD CONSTRAINT proposition_pkey PRIMARY KEY (id_concept, id_user, id_thesaurus);


--
-- TOC entry 2816 (class 2606 OID 29493)
-- Name: roles role_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 2818 (class 2606 OID 29495)
-- Name: routine_mail routine_mail_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.routine_mail
    ADD CONSTRAINT routine_mail_pkey PRIMARY KEY (id_thesaurus);


--
-- TOC entry 2827 (class 2606 OID 29497)
-- Name: term_candidat term_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term_candidat
    ADD CONSTRAINT term_candidat_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus, contributor);


--
-- TOC entry 2830 (class 2606 OID 109093)
-- Name: term_historique term_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term_historique
    ADD CONSTRAINT term_copy_pkey PRIMARY KEY (id, modified, id_user);


--
-- TOC entry 2821 (class 2606 OID 29501)
-- Name: term term_id_term_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term
    ADD CONSTRAINT term_id_term_key UNIQUE (id_term, lang, id_thesaurus);


--
-- TOC entry 2823 (class 2606 OID 29503)
-- Name: term term_id_term_lexical_value_lang_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term
    ADD CONSTRAINT term_id_term_lexical_value_lang_id_thesaurus_key UNIQUE (id_term, lexical_value, lang, id_thesaurus);


--
-- TOC entry 2825 (class 2606 OID 29505)
-- Name: term term_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term
    ADD CONSTRAINT term_pkey PRIMARY KEY (id);


--
-- TOC entry 2834 (class 2606 OID 29507)
-- Name: thesaurus_alignement_source thesaurus_alignement_source_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_alignement_source
    ADD CONSTRAINT thesaurus_alignement_source_pkey PRIMARY KEY (id_thesaurus, id_alignement_source);


--
-- TOC entry 2838 (class 2606 OID 29509)
-- Name: thesaurus_array_concept thesaurus_array_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_array_concept
    ADD CONSTRAINT thesaurus_array_concept_pkey PRIMARY KEY (thesaurusarrayid, id_concept, id_thesaurus);


--
-- TOC entry 2836 (class 2606 OID 29511)
-- Name: thesaurus_array thesaurus_array_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_array
    ADD CONSTRAINT thesaurus_array_pkey PRIMARY KEY (facet_id, id_thesaurus, id_concept_parent);


--
-- TOC entry 2840 (class 2606 OID 29513)
-- Name: thesaurus_label thesaurus_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_label
    ADD CONSTRAINT thesaurus_pkey PRIMARY KEY (id_thesaurus, lang, title);


--
-- TOC entry 2832 (class 2606 OID 29515)
-- Name: thesaurus thesaurus_pkey1; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus
    ADD CONSTRAINT thesaurus_pkey1 PRIMARY KEY (id_thesaurus, id_ark);


--
-- TOC entry 2842 (class 2606 OID 29517)
-- Name: thesaurus_label unique_thesau_lang; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_label
    ADD CONSTRAINT unique_thesau_lang UNIQUE (id_thesaurus, lang);


--
-- TOC entry 2864 (class 2606 OID 46267)
-- Name: user_group_label user_group-label_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_group_label
    ADD CONSTRAINT "user_group-label_pkey" PRIMARY KEY (id_group);


--
-- TOC entry 2860 (class 2606 OID 64522)
-- Name: user_group_thesaurus user_group_thesaurus_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_group_thesaurus
    ADD CONSTRAINT user_group_thesaurus_id_thesaurus_key UNIQUE (id_thesaurus);


--
-- TOC entry 2862 (class 2606 OID 64516)
-- Name: user_group_thesaurus user_group_thesaurus_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_group_thesaurus
    ADD CONSTRAINT user_group_thesaurus_pkey PRIMARY KEY (id_group, id_thesaurus);


--
-- TOC entry 2844 (class 2606 OID 29519)
-- Name: users user_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_pkey PRIMARY KEY (id_user);


--
-- TOC entry 2868 (class 2606 OID 64530)
-- Name: user_role_only_on user_role_only_on_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_role_only_on
    ADD CONSTRAINT user_role_only_on_pkey PRIMARY KEY (id_user, id_role, id_theso);


--
-- TOC entry 2866 (class 2606 OID 46294)
-- Name: user_role_group user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_role_group
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (id_user, id_role, id_group);


--
-- TOC entry 2856 (class 2606 OID 29523)
-- Name: users_historique users_historique_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users_historique
    ADD CONSTRAINT users_historique_pkey PRIMARY KEY (id_user);


--
-- TOC entry 2850 (class 2606 OID 29525)
-- Name: users2 users_login_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users2
    ADD CONSTRAINT users_login_key UNIQUE (login);


--
-- TOC entry 2852 (class 2606 OID 29527)
-- Name: users2 users_mail_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users2
    ADD CONSTRAINT users_mail_key UNIQUE (mail);


--
-- TOC entry 2846 (class 2606 OID 29529)
-- Name: users users_mail_key1; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_mail_key1 UNIQUE (mail);


--
-- TOC entry 2854 (class 2606 OID 29531)
-- Name: users2 users_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users2
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


--
-- TOC entry 2848 (class 2606 OID 29533)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 2819 (class 1259 OID 29534)
-- Name: index_lexical_value; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX index_lexical_value ON public.term USING btree (lexical_value);


--
-- TOC entry 2828 (class 1259 OID 29535)
-- Name: index_lexical_value_copy; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX index_lexical_value_copy ON public.term_historique USING btree (lexical_value);


--
-- TOC entry 2800 (class 1259 OID 29536)
-- Name: permuted_lexical_value_idx; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX permuted_lexical_value_idx ON public.permuted USING btree (lexical_value);


-- Completed on 2018-08-02 11:45:25 CEST

--
-- PostgreSQL database dump complete
--
