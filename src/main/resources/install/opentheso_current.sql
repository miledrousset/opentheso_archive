--
-- PostgreSQL database dump
--

-- Dumped from database version 11.0
-- Dumped by pg_dump version 11.0

-- Started on 2019-01-09 13:43:08 CET

-- version=4.4.2

SET role = opentheso;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 2 (class 3079 OID 33871)
-- Name: pg_trgm; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;


--
-- TOC entry 3936 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION pg_trgm; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pg_trgm IS 'text similarity measurement and index searching based on trigrams';


--
-- TOC entry 3 (class 3079 OID 33848)
-- Name: unaccent; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS unaccent WITH SCHEMA public;


--
-- TOC entry 3937 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION unaccent; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION unaccent IS 'text search dictionary that removes accents';


--
-- TOC entry 715 (class 1247 OID 16389)
-- Name: alignement_format; Type: TYPE; Schema: public; Owner: opentheso
--

CREATE TYPE public.alignement_format AS ENUM (
    'skos',
    'json',
    'xml'
);


ALTER TYPE public.alignement_format OWNER TO opentheso;

--
-- TOC entry 718 (class 1247 OID 16396)
-- Name: alignement_type_rqt; Type: TYPE; Schema: public; Owner: opentheso
--

CREATE TYPE public.alignement_type_rqt AS ENUM (
    'SPARQL',
    'REST'
);


ALTER TYPE public.alignement_type_rqt OWNER TO opentheso;

--
-- TOC entry 721 (class 1247 OID 16402)
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
-- TOC entry 325 (class 1255 OID 16411)
-- Name: addconstraintto_alignement(); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.addconstraintto_alignement() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
    IF EXISTS(SELECT * FROM information_schema.constraint_table_usage where constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_id_alignem') THEN
        execute 'ALTER TABLE alignement DROP CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_id_alignem;';
    END IF;
    IF EXISTS(SELECT * FROM information_schema.constraint_table_usage where constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_id_ali_key') THEN
        execute 'ALTER TABLE alignement DROP CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_id_ali_key;';
    END IF;
    
   
    IF NOT EXISTS(SELECT * FROM information_schema.constraint_table_usage where constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_uri_ta_key') THEN
        execute 'alter TABLE ONLY alignement add CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_uri_ta_key
	UNIQUE (internal_id_concept, internal_id_thesaurus, uri_target);';
    END IF;
end
$$;


ALTER FUNCTION public.addconstraintto_alignement() OWNER TO opentheso;

--
-- TOC entry 326 (class 1255 OID 16412)
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
-- TOC entry 327 (class 1255 OID 16413)
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
-- TOC entry 328 (class 1255 OID 16414)
-- Name: drop_constraint_alignement_source(); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.drop_constraint_alignement_source() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
	if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_concept_target_thesaurus_target_alignement_id_ty_key') then 
	execute
	'ALTER TABLE ONLY alignement
	  DROP CONSTRAINT alignement_concept_target_thesaurus_target_alignement_id_ty_key ;';
  end if;
  end;
  $$;


ALTER FUNCTION public.drop_constraint_alignement_source() OWNER TO opentheso;

--
-- TOC entry 334 (class 1255 OID 33957)
-- Name: f_unaccent(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.f_unaccent(text) RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $_$
SELECT public.unaccent('public.unaccent', $1)
$_$;


ALTER FUNCTION public.f_unaccent(text) OWNER TO postgres;

--
-- TOC entry 331 (class 1255 OID 16415)
-- Name: id_alignements(); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.id_alignements() RETURNS void
    LANGUAGE plpgsql
    AS $$
declare
	p_data text[]:='{}';
	nom text;
	cherchesource text;
	source text;
	source_recuperated text;
	positio1 integer;
	positio2 integer;
	total integer;
	cherche integer;
	id integer:=0;
	begin 
		for nom in 
		select uri_target from alignement where id_alignement_source = 0
		loop
			 select position ('//' in nom) into positio1;
			 select substring (nom from positio1+2) into source_recuperated;
			 select position ('/' in source_recuperated) into positio2;
			 total := positio2 - positio1;
			 select substring (source_recuperated from 0 for positio2) into source_recuperated;
			for cherchesource in 
			select alignement_source.source from alignement_source
			loop
				if(source_recuperated = cherchesource) then
					select alignement_source.id from alignement_source where alignement_source.source = cherchesource into id;
				execute
					'update alignement set  id_alignement_source = '||id||' where uri_target = '||quote_literal(nom)||' and id_alignement_source = 0;';
				end if;
			end loop;
			if (id=0) then	
				execute
				'INSERT INTO alignement_source (source, requete, type_rqt, alignement_format) VALUES ('''||source_recuperated||''', ''null'', ''REST'', ''xml'');';
				SELECT max(alignement_source.id) from alignement_source  into id;
				execute
				'update alignement set  id_alignement_source = '||id||' where uri_target = '||quote_literal(nom)||';';
				
			end if;
			id:=0;	
		end loop;
	end;
	$$;


ALTER FUNCTION public.id_alignements() OWNER TO opentheso;

--
-- TOC entry 332 (class 1255 OID 16416)
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
-- TOC entry 333 (class 1255 OID 16417)
-- Name: update_table_preferences_preferredname(); Type: FUNCTION; Schema: public; Owner: opentheso
--

CREATE FUNCTION public.update_table_preferences_preferredname() RETURNS void
    LANGUAGE plpgsql
    AS $$
    declare
	line RECORD;
	begin 
            IF NOT EXISTS (SELECT * FROM information_schema.columns WHERE table_name='preferences' AND column_name='preferredname' ) THEN
                execute 'Alter TABLE preferences ADD COLUMN preferredname character varying;
                        Alter TABLE ONLY preferences add CONSTRAINT preferences_preferredname_key UNIQUE (preferredname);';
            end if;
	end;
$$;


ALTER FUNCTION public.update_table_preferences_preferredname() OWNER TO opentheso;

--
-- TOC entry 198 (class 1259 OID 16418)
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
-- TOC entry 199 (class 1259 OID 16420)
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
-- TOC entry 200 (class 1259 OID 16429)
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
-- TOC entry 201 (class 1259 OID 16431)
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
-- TOC entry 202 (class 1259 OID 16438)
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
-- TOC entry 203 (class 1259 OID 16440)
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
-- TOC entry 204 (class 1259 OID 16448)
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
-- TOC entry 205 (class 1259 OID 16454)
-- Name: bt_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.bt_type (
    id integer NOT NULL,
    relation character varying,
    description_fr character varying,
    description_en character varying
);


ALTER TABLE public.bt_type OWNER TO opentheso;

--
-- TOC entry 206 (class 1259 OID 16460)
-- Name: compound_equivalence; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.compound_equivalence (
    id_split_nonpreferredterm text NOT NULL,
    id_preferredterm text NOT NULL
);


ALTER TABLE public.compound_equivalence OWNER TO opentheso;

--
-- TOC entry 207 (class 1259 OID 16466)
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
-- TOC entry 208 (class 1259 OID 16468)
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
-- TOC entry 209 (class 1259 OID 16481)
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
-- TOC entry 210 (class 1259 OID 16483)
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
-- TOC entry 211 (class 1259 OID 16493)
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
-- TOC entry 212 (class 1259 OID 16500)
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
-- TOC entry 213 (class 1259 OID 16502)
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
-- TOC entry 214 (class 1259 OID 16511)
-- Name: concept_group_concept; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_concept (
    idgroup text NOT NULL,
    idthesaurus text NOT NULL,
    idconcept text NOT NULL
);


ALTER TABLE public.concept_group_concept OWNER TO opentheso;

--
-- TOC entry 215 (class 1259 OID 16517)
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
-- TOC entry 216 (class 1259 OID 16519)
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
-- TOC entry 217 (class 1259 OID 16527)
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
-- TOC entry 218 (class 1259 OID 16529)
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
-- TOC entry 219 (class 1259 OID 16538)
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
-- TOC entry 220 (class 1259 OID 16540)
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
-- TOC entry 221 (class 1259 OID 16548)
-- Name: concept_group_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_group_type (
    code text NOT NULL,
    label text NOT NULL,
    skoslabel text
);


ALTER TABLE public.concept_group_type OWNER TO opentheso;

--
-- TOC entry 222 (class 1259 OID 16554)
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
-- TOC entry 223 (class 1259 OID 16556)
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
-- TOC entry 224 (class 1259 OID 16565)
-- Name: concept_orphan; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_orphan (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.concept_orphan OWNER TO opentheso;

--
-- TOC entry 225 (class 1259 OID 16571)
-- Name: concept_term_candidat; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.concept_term_candidat (
    id_concept character varying NOT NULL,
    id_term character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.concept_term_candidat OWNER TO opentheso;

--
-- TOC entry 226 (class 1259 OID 16577)
-- Name: copyright; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.copyright (
    id_thesaurus character varying NOT NULL,
    copyright character varying
);


ALTER TABLE public.copyright OWNER TO opentheso;

--
-- TOC entry 227 (class 1259 OID 16583)
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
-- TOC entry 228 (class 1259 OID 16589)
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
-- TOC entry 229 (class 1259 OID 16595)
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
-- TOC entry 230 (class 1259 OID 16597)
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
-- TOC entry 231 (class 1259 OID 16603)
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
-- TOC entry 232 (class 1259 OID 16605)
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
-- TOC entry 233 (class 1259 OID 16615)
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
-- TOC entry 234 (class 1259 OID 16621)
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
-- TOC entry 235 (class 1259 OID 16628)
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
-- TOC entry 236 (class 1259 OID 16634)
-- Name: info; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.info (
    version_opentheso character varying NOT NULL,
    version_bdd character varying NOT NULL
);


ALTER TABLE public.info OWNER TO opentheso;

--
-- TOC entry 237 (class 1259 OID 16640)
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
-- TOC entry 238 (class 1259 OID 16642)
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
-- TOC entry 239 (class 1259 OID 16649)
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
-- TOC entry 240 (class 1259 OID 16657)
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
-- TOC entry 241 (class 1259 OID 16666)
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
-- TOC entry 242 (class 1259 OID 16674)
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
-- TOC entry 243 (class 1259 OID 16676)
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
-- TOC entry 244 (class 1259 OID 16685)
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
-- TOC entry 245 (class 1259 OID 16687)
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
-- TOC entry 246 (class 1259 OID 16695)
-- Name: note_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.note_type (
    code text NOT NULL,
    isterm boolean NOT NULL,
    isconcept boolean NOT NULL,
    label_fr character varying,
    label_en character varying,
    CONSTRAINT chk_not_false_values CHECK ((NOT ((isterm = false) AND (isconcept = false))))
);


ALTER TABLE public.note_type OWNER TO opentheso;

--
-- TOC entry 247 (class 1259 OID 16702)
-- Name: nt_type; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.nt_type (
    id integer NOT NULL,
    relation character varying,
    description_fr character varying,
    description_en character varying
);


ALTER TABLE public.nt_type OWNER TO opentheso;

--
-- TOC entry 248 (class 1259 OID 16708)
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
-- TOC entry 249 (class 1259 OID 16714)
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
-- TOC entry 250 (class 1259 OID 16716)
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
    private_prefix_handle character varying DEFAULT 'crt'::character varying NOT NULL,
    preferredname character varying
);


ALTER TABLE public.preferences OWNER TO opentheso;

--
-- TOC entry 251 (class 1259 OID 16749)
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
-- TOC entry 252 (class 1259 OID 16756)
-- Name: preferred_term; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.preferred_term (
    id_concept character varying NOT NULL,
    id_term character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.preferred_term OWNER TO opentheso;

--
-- TOC entry 253 (class 1259 OID 16762)
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
-- TOC entry 254 (class 1259 OID 16770)
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
-- TOC entry 255 (class 1259 OID 16776)
-- Name: roles; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.roles (
    id integer NOT NULL,
    name character varying,
    description character varying
);


ALTER TABLE public.roles OWNER TO opentheso;

--
-- TOC entry 256 (class 1259 OID 16782)
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
-- TOC entry 3938 (class 0 OID 0)
-- Dependencies: 256
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: opentheso
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.roles.id;


--
-- TOC entry 257 (class 1259 OID 16784)
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
-- TOC entry 258 (class 1259 OID 16791)
-- Name: split_non_preferred_term; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.split_non_preferred_term (
);


ALTER TABLE public.split_non_preferred_term OWNER TO opentheso;

--
-- TOC entry 259 (class 1259 OID 16794)
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
-- TOC entry 260 (class 1259 OID 16796)
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
-- TOC entry 261 (class 1259 OID 16806)
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
-- TOC entry 262 (class 1259 OID 16808)
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
-- TOC entry 263 (class 1259 OID 16817)
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
-- TOC entry 264 (class 1259 OID 16819)
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
-- TOC entry 265 (class 1259 OID 16828)
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
-- TOC entry 266 (class 1259 OID 16830)
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
-- TOC entry 267 (class 1259 OID 16840)
-- Name: thesaurus_alignement_source; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.thesaurus_alignement_source (
    id_thesaurus character varying NOT NULL,
    id_alignement_source integer NOT NULL
);


ALTER TABLE public.thesaurus_alignement_source OWNER TO opentheso;

--
-- TOC entry 268 (class 1259 OID 16846)
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
-- TOC entry 269 (class 1259 OID 16854)
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
-- TOC entry 270 (class 1259 OID 16861)
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
-- TOC entry 271 (class 1259 OID 16869)
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
-- TOC entry 272 (class 1259 OID 16871)
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
-- TOC entry 273 (class 1259 OID 16873)
-- Name: user_group_label; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_group_label (
    id_group integer DEFAULT nextval('public.user_group_label__id_seq'::regclass) NOT NULL,
    label_group character varying
);


ALTER TABLE public.user_group_label OWNER TO opentheso;

--
-- TOC entry 274 (class 1259 OID 16880)
-- Name: user_group_thesaurus; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_group_thesaurus (
    id_group integer NOT NULL,
    id_thesaurus character varying NOT NULL
);


ALTER TABLE public.user_group_thesaurus OWNER TO opentheso;

--
-- TOC entry 275 (class 1259 OID 16886)
-- Name: user_role_group; Type: TABLE; Schema: public; Owner: opentheso
--

CREATE TABLE public.user_role_group (
    id_user integer NOT NULL,
    id_role integer NOT NULL,
    id_group integer NOT NULL
);


ALTER TABLE public.user_role_group OWNER TO opentheso;

--
-- TOC entry 276 (class 1259 OID 16889)
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
-- TOC entry 277 (class 1259 OID 16896)
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
-- TOC entry 278 (class 1259 OID 16907)
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
-- TOC entry 279 (class 1259 OID 16916)
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
-- TOC entry 280 (class 1259 OID 16924)
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
-- TOC entry 3527 (class 2604 OID 16930)
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- TOC entry 3849 (class 0 OID 16420)
-- Dependencies: 199
-- Data for Name: alignement; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3851 (class 0 OID 16431)
-- Dependencies: 201
-- Data for Name: alignement_preferences; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3853 (class 0 OID 16440)
-- Dependencies: 203
-- Data for Name: alignement_source; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Pactols', 'https://pactols.frantiq.fr/opentheso/webresources/rest/skos/concept/value=##value##&lang=##lang##&th=TH_1', 'REST', 'skos', 1, NULL, '', false);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('GeoNames', 'http://api.geonames.org/search?q=##value##&maxRows=10&style=FULL&lang=##lang##&username=opentheso', 'REST', 'xml', 2, NULL, 'test de geonames', true);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Gemet', 'https://www.eionet.europa.eu/gemet/getConceptsMatchingKeyword?keyword=##value##&search_mode=3&thesaurus_uri=http://www.eionet.europa.eu/gemet/concept/&language=##lang##', 'REST', 'json', 4, NULL, '', false);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('bnf_instrumentMusique', 'PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX xml: <http://www.w3.org/XML/1998/namespace>
SELECT ?instrument ?prop ?value where {
  <http://data.bnf.fr/ark:/12148/cb119367821> skos:narrower+ ?instrument.
  ?instrument ?prop ?value.
  FILTER( (regex(?prop,skos:prefLabel) || regex(?prop,skos:altLabel))  && regex(?value, ##value##,"i") ) 
    filter(lang(?value) =##lang##)
} LIMIT 20', 'SPARQL', 'skos', 5, NULL, '', false);
INSERT INTO public.alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('wikidata', 'SELECT ?item ?itemLabel ?itemDescription WHERE {

            ?item rdfs:label "##value##"@##lang##.

            SERVICE wikibase:label { bd:serviceParam wikibase:language "[AUTO_LANGUAGE],##lang##". }

}', 'SPARQL', 'json', 3, 9, 'alignement avec le thésaurus de wikidata', false);


--
-- TOC entry 3854 (class 0 OID 16448)
-- Dependencies: 204
-- Data for Name: alignement_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (1, 'Equivalence exacte', '=EQ', 'exactMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (2, 'Equivalence inexacte', '~EQ', 'closeMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (3, 'Equivalence générique', 'EQB', 'broadMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (4, 'Equivalence associative', 'EQR', 'relatedMatch');
INSERT INTO public.alignement_type (id, label, isocode, label_skos) VALUES (5, 'Equivalence spécifique', 'EQS', 'narrowMatch');


--
-- TOC entry 3855 (class 0 OID 16454)
-- Dependencies: 205
-- Data for Name: bt_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.bt_type (id, relation, description_fr, description_en) VALUES (1, 'BT', 'Terme générique', 'Broader term');
INSERT INTO public.bt_type (id, relation, description_fr, description_en) VALUES (2, 'BTG', 'Terme générique (generic)', 'Broader term (generic)');
INSERT INTO public.bt_type (id, relation, description_fr, description_en) VALUES (3, 'BTP', 'Terme générique (partitive)', 'Broader term (partitive)');
INSERT INTO public.bt_type (id, relation, description_fr, description_en) VALUES (4, 'BTI', 'Terme générique (instance)', 'Broader term (instance)');


--
-- TOC entry 3856 (class 0 OID 16460)
-- Dependencies: 206
-- Data for Name: compound_equivalence; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3858 (class 0 OID 16468)
-- Dependencies: 208
-- Data for Name: concept; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3860 (class 0 OID 16483)
-- Dependencies: 210
-- Data for Name: concept_candidat; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3861 (class 0 OID 16493)
-- Dependencies: 211
-- Data for Name: concept_fusion; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3863 (class 0 OID 16502)
-- Dependencies: 213
-- Data for Name: concept_group; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3864 (class 0 OID 16511)
-- Dependencies: 214
-- Data for Name: concept_group_concept; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3866 (class 0 OID 16519)
-- Dependencies: 216
-- Data for Name: concept_group_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3868 (class 0 OID 16529)
-- Dependencies: 218
-- Data for Name: concept_group_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3870 (class 0 OID 16540)
-- Dependencies: 220
-- Data for Name: concept_group_label_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3871 (class 0 OID 16548)
-- Dependencies: 221
-- Data for Name: concept_group_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('MT', 'Microthesaurus', 'MicroThesaurus');
INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('G', 'Group', 'ConceptGroup');
INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('C', 'Collection', 'Collection');
INSERT INTO public.concept_group_type (code, label, skoslabel) VALUES ('T', 'Theme', 'Theme');


--
-- TOC entry 3873 (class 0 OID 16556)
-- Dependencies: 223
-- Data for Name: concept_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3874 (class 0 OID 16565)
-- Dependencies: 224
-- Data for Name: concept_orphan; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3875 (class 0 OID 16571)
-- Dependencies: 225
-- Data for Name: concept_term_candidat; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3876 (class 0 OID 16577)
-- Dependencies: 226
-- Data for Name: copyright; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3877 (class 0 OID 16583)
-- Dependencies: 227
-- Data for Name: custom_concept_attribute; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3878 (class 0 OID 16589)
-- Dependencies: 228
-- Data for Name: custom_term_attribute; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3880 (class 0 OID 16597)
-- Dependencies: 230
-- Data for Name: gps; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3882 (class 0 OID 16605)
-- Dependencies: 232
-- Data for Name: gps_preferences; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3883 (class 0 OID 16615)
-- Dependencies: 233
-- Data for Name: hierarchical_relationship; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3884 (class 0 OID 16621)
-- Dependencies: 234
-- Data for Name: hierarchical_relationship_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3885 (class 0 OID 16628)
-- Dependencies: 235
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3886 (class 0 OID 16634)
-- Dependencies: 236
-- Data for Name: info; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3888 (class 0 OID 16642)
-- Dependencies: 238
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
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('iso', 'iso', 'norme ISO 233-2 (1993)', 'norme ISO 233-2 (1993)', 187);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ala', 'ala', 'ALA-LC Romanization Table (American Library Association-Library of Congress)', 'ALA-LC)', 188);
INSERT INTO public.languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mul', 'mul', 'multiple langages', 'multiple langages', 189);


--
-- TOC entry 3889 (class 0 OID 16649)
-- Dependencies: 239
-- Data for Name: node_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3890 (class 0 OID 16657)
-- Dependencies: 240
-- Data for Name: non_preferred_term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3891 (class 0 OID 16666)
-- Dependencies: 241
-- Data for Name: non_preferred_term_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3893 (class 0 OID 16676)
-- Dependencies: 243
-- Data for Name: note; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3895 (class 0 OID 16687)
-- Dependencies: 245
-- Data for Name: note_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3896 (class 0 OID 16695)
-- Dependencies: 246
-- Data for Name: note_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('note', false, true, 'Note', 'Note');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('historyNote', true, true, 'Note historique', 'History note');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('scopeNote', false, true, 'Note de portée', 'Scope note');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('example', true, false, 'Exemple', 'Example');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('editorialNote', true, false, 'Note éditoriale', 'Editorial note');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('definition', true, false, 'Définition', 'Definition');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('customNote', false, true, 'Custom note', 'Custom note');
INSERT INTO public.note_type (code, isterm, isconcept, label_fr, label_en) VALUES ('changeNote', true, false, 'Note de changement', 'Change note');


--
-- TOC entry 3897 (class 0 OID 16702)
-- Dependencies: 247
-- Data for Name: nt_type; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.nt_type (id, relation, description_fr, description_en) VALUES (1, 'NT', 'Term spécifique', 'Narrower term');
INSERT INTO public.nt_type (id, relation, description_fr, description_en) VALUES (2, 'NTG', 'Term spécifique (generic)', 'Narrower term (generic)');
INSERT INTO public.nt_type (id, relation, description_fr, description_en) VALUES (3, 'NTP', 'Term spécifique (partitive)', 'Narrower term (partitive)');
INSERT INTO public.nt_type (id, relation, description_fr, description_en) VALUES (4, 'NTI', 'Term spécifique (instantial)', 'Narrower term (instantial)');


--
-- TOC entry 3898 (class 0 OID 16708)
-- Dependencies: 248
-- Data for Name: permuted; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3900 (class 0 OID 16716)
-- Dependencies: 250
-- Data for Name: preferences; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3901 (class 0 OID 16749)
-- Dependencies: 251
-- Data for Name: preferences_sparql; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3902 (class 0 OID 16756)
-- Dependencies: 252
-- Data for Name: preferred_term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3903 (class 0 OID 16762)
-- Dependencies: 253
-- Data for Name: proposition; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3904 (class 0 OID 16770)
-- Dependencies: 254
-- Data for Name: relation_group; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3905 (class 0 OID 16776)
-- Dependencies: 255
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.roles (id, name, description) VALUES (1, 'superAdmin', 'Super Administrateur pour tout gérer tout thésaurus et tout utilisateur');
INSERT INTO public.roles (id, name, description) VALUES (2, 'admin', 'administrateur pour un domaine ou parc de thésaurus');
INSERT INTO public.roles (id, name, description) VALUES (3, 'manager', 'gestionnaire de thésaurus, pas de création de thésaurus');
INSERT INTO public.roles (id, name, description) VALUES (4, 'contributor', 'traducteur, notes, candidats, images');


--
-- TOC entry 3907 (class 0 OID 16784)
-- Dependencies: 257
-- Data for Name: routine_mail; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3908 (class 0 OID 16791)
-- Dependencies: 258
-- Data for Name: split_non_preferred_term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3910 (class 0 OID 16796)
-- Dependencies: 260
-- Data for Name: term; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3912 (class 0 OID 16808)
-- Dependencies: 262
-- Data for Name: term_candidat; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3914 (class 0 OID 16819)
-- Dependencies: 264
-- Data for Name: term_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3916 (class 0 OID 16830)
-- Dependencies: 266
-- Data for Name: thesaurus; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3917 (class 0 OID 16840)
-- Dependencies: 267
-- Data for Name: thesaurus_alignement_source; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3918 (class 0 OID 16846)
-- Dependencies: 268
-- Data for Name: thesaurus_array; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3919 (class 0 OID 16854)
-- Dependencies: 269
-- Data for Name: thesaurus_array_concept; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3920 (class 0 OID 16861)
-- Dependencies: 270
-- Data for Name: thesaurus_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3923 (class 0 OID 16873)
-- Dependencies: 273
-- Data for Name: user_group_label; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3924 (class 0 OID 16880)
-- Dependencies: 274
-- Data for Name: user_group_thesaurus; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3925 (class 0 OID 16886)
-- Dependencies: 275
-- Data for Name: user_role_group; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3926 (class 0 OID 16889)
-- Dependencies: 276
-- Data for Name: user_role_only_on; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3927 (class 0 OID 16896)
-- Dependencies: 277
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.users (id_user, username, password, active, mail, passtomodify, alertmail, issuperadmin) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', true, 'admin@domaine.fr', false, false, true);


--
-- TOC entry 3928 (class 0 OID 16907)
-- Dependencies: 278
-- Data for Name: users2; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO public.users2 (id_user, login, fullname, password, active, mail, authentication) VALUES (8, 'miled', 'Miled Rousset', '', true, 'mie@mile', 'DB');
INSERT INTO public.users2 (id_user, login, fullname, password, active, mail, authentication) VALUES (9, 'toto', 'toeot', '', true, 'mil@mf', 'LDAP');


--
-- TOC entry 3929 (class 0 OID 16916)
-- Dependencies: 279
-- Data for Name: users_historique; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3930 (class 0 OID 16924)
-- Dependencies: 280
-- Data for Name: version_history; Type: TABLE DATA; Schema: public; Owner: opentheso
--



--
-- TOC entry 3939 (class 0 OID 0)
-- Dependencies: 198
-- Name: alignement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.alignement_id_seq', 1, false);


--
-- TOC entry 3940 (class 0 OID 0)
-- Dependencies: 200
-- Name: alignement_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.alignement_preferences_id_seq', 1, false);


--
-- TOC entry 3941 (class 0 OID 0)
-- Dependencies: 202
-- Name: alignement_source__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.alignement_source__id_seq', 13, false);


--
-- TOC entry 3942 (class 0 OID 0)
-- Dependencies: 207
-- Name: concept__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept__id_seq', 1, false);


--
-- TOC entry 3943 (class 0 OID 0)
-- Dependencies: 209
-- Name: concept_candidat__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_candidat__id_seq', 1, false);


--
-- TOC entry 3944 (class 0 OID 0)
-- Dependencies: 212
-- Name: concept_group__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group__id_seq', 1, false);


--
-- TOC entry 3945 (class 0 OID 0)
-- Dependencies: 215
-- Name: concept_group_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group_historique__id_seq', 1, false);


--
-- TOC entry 3946 (class 0 OID 0)
-- Dependencies: 219
-- Name: concept_group_label_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group_label_historique__id_seq', 1, false);


--
-- TOC entry 3947 (class 0 OID 0)
-- Dependencies: 217
-- Name: concept_group_label_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_group_label_id_seq', 1, false);


--
-- TOC entry 3948 (class 0 OID 0)
-- Dependencies: 222
-- Name: concept_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.concept_historique__id_seq', 1, false);


--
-- TOC entry 3949 (class 0 OID 0)
-- Dependencies: 229
-- Name: facet_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.facet_id_seq', 1, false);


--
-- TOC entry 3950 (class 0 OID 0)
-- Dependencies: 231
-- Name: gps_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.gps_preferences_id_seq', 1, false);


--
-- TOC entry 3951 (class 0 OID 0)
-- Dependencies: 237
-- Name: languages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.languages_id_seq', 189, false);


--
-- TOC entry 3952 (class 0 OID 0)
-- Dependencies: 242
-- Name: note__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.note__id_seq', 1, false);


--
-- TOC entry 3953 (class 0 OID 0)
-- Dependencies: 244
-- Name: note_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.note_historique__id_seq', 1, false);


--
-- TOC entry 3954 (class 0 OID 0)
-- Dependencies: 249
-- Name: pref__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.pref__id_seq', 1, false);


--
-- TOC entry 3955 (class 0 OID 0)
-- Dependencies: 256
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.role_id_seq', 6, true);


--
-- TOC entry 3956 (class 0 OID 0)
-- Dependencies: 259
-- Name: term__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.term__id_seq', 1, false);


--
-- TOC entry 3957 (class 0 OID 0)
-- Dependencies: 261
-- Name: term_candidat__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.term_candidat__id_seq', 1, false);


--
-- TOC entry 3958 (class 0 OID 0)
-- Dependencies: 263
-- Name: term_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.term_historique__id_seq', 1, false);


--
-- TOC entry 3959 (class 0 OID 0)
-- Dependencies: 265
-- Name: thesaurus_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.thesaurus_id_seq', 1, false);


--
-- TOC entry 3960 (class 0 OID 0)
-- Dependencies: 271
-- Name: user__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.user__id_seq', 2, false);


--
-- TOC entry 3961 (class 0 OID 0)
-- Dependencies: 272
-- Name: user_group_label__id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('public.user_group_label__id_seq', 1, false);


--
-- TOC entry 3726 (class 2606 OID 16932)
-- Name: version_history VersionHistory_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.version_history
    ADD CONSTRAINT "VersionHistory_pkey" PRIMARY KEY ("idVersionhistory");


--
-- TOC entry 3561 (class 2606 OID 16934)
-- Name: alignement alignement_internal_id_concept_internal_id_thesaurus_uri_ta_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement
    ADD CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_uri_ta_key UNIQUE (internal_id_concept, internal_id_thesaurus, uri_target);


--
-- TOC entry 3563 (class 2606 OID 16936)
-- Name: alignement alignement_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement
    ADD CONSTRAINT alignement_pkey PRIMARY KEY (id);


--
-- TOC entry 3565 (class 2606 OID 16938)
-- Name: alignement_preferences alignement_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_preferences
    ADD CONSTRAINT alignement_preferences_pkey PRIMARY KEY (id_thesaurus, id_user, id_concept_depart, id_alignement_source);


--
-- TOC entry 3567 (class 2606 OID 16940)
-- Name: alignement_source alignement_source_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_source
    ADD CONSTRAINT alignement_source_pkey PRIMARY KEY (id);


--
-- TOC entry 3569 (class 2606 OID 16942)
-- Name: alignement_source alignement_source_source_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_source
    ADD CONSTRAINT alignement_source_source_key UNIQUE (source);


--
-- TOC entry 3571 (class 2606 OID 16944)
-- Name: alignement_type alignment_type_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.alignement_type
    ADD CONSTRAINT alignment_type_pkey PRIMARY KEY (id);


--
-- TOC entry 3573 (class 2606 OID 16946)
-- Name: bt_type bt_type_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.bt_type
    ADD CONSTRAINT bt_type_pkey PRIMARY KEY (id);


--
-- TOC entry 3575 (class 2606 OID 16948)
-- Name: bt_type bt_type_relation_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.bt_type
    ADD CONSTRAINT bt_type_relation_key UNIQUE (relation);


--
-- TOC entry 3577 (class 2606 OID 16950)
-- Name: compound_equivalence compound_equivalence_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.compound_equivalence
    ADD CONSTRAINT compound_equivalence_pkey PRIMARY KEY (id_split_nonpreferredterm, id_preferredterm);


--
-- TOC entry 3582 (class 2606 OID 16952)
-- Name: concept_candidat concept_candidat_id_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_candidat
    ADD CONSTRAINT concept_candidat_id_key UNIQUE (id);


--
-- TOC entry 3584 (class 2606 OID 16954)
-- Name: concept_candidat concept_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_candidat
    ADD CONSTRAINT concept_candidat_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 3604 (class 2606 OID 16956)
-- Name: concept_historique concept_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_historique
    ADD CONSTRAINT concept_copy_pkey PRIMARY KEY (id_concept, id_thesaurus, id_group, id_user, modified);


--
-- TOC entry 3586 (class 2606 OID 16958)
-- Name: concept_fusion concept_fusion_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_fusion
    ADD CONSTRAINT concept_fusion_pkey PRIMARY KEY (id_concept1, id_concept2, id_thesaurus);


--
-- TOC entry 3590 (class 2606 OID 16960)
-- Name: concept_group_concept concept_group_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_concept
    ADD CONSTRAINT concept_group_concept_pkey PRIMARY KEY (idgroup, idthesaurus, idconcept);


--
-- TOC entry 3592 (class 2606 OID 16962)
-- Name: concept_group_historique concept_group_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_historique
    ADD CONSTRAINT concept_group_copy_pkey PRIMARY KEY (idgroup, idthesaurus, modified, id_user);


--
-- TOC entry 3598 (class 2606 OID 16964)
-- Name: concept_group_label_historique concept_group_label_copy_idgrouplabel_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label_historique
    ADD CONSTRAINT concept_group_label_copy_idgrouplabel_key UNIQUE (id);


--
-- TOC entry 3600 (class 2606 OID 16966)
-- Name: concept_group_label_historique concept_group_label_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label_historique
    ADD CONSTRAINT concept_group_label_copy_pkey PRIMARY KEY (lang, idthesaurus, lexicalvalue, modified, id_user);


--
-- TOC entry 3594 (class 2606 OID 16968)
-- Name: concept_group_label concept_group_label_idgrouplabel_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label
    ADD CONSTRAINT concept_group_label_idgrouplabel_key UNIQUE (id);


--
-- TOC entry 3596 (class 2606 OID 16970)
-- Name: concept_group_label concept_group_label_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_label
    ADD CONSTRAINT concept_group_label_pkey PRIMARY KEY (lang, idthesaurus, lexicalvalue);


--
-- TOC entry 3588 (class 2606 OID 16972)
-- Name: concept_group concept_group_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group
    ADD CONSTRAINT concept_group_pkey PRIMARY KEY (idgroup, idthesaurus);


--
-- TOC entry 3602 (class 2606 OID 16974)
-- Name: concept_group_type concept_group_type_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_group_type
    ADD CONSTRAINT concept_group_type_pkey PRIMARY KEY (code, label);


--
-- TOC entry 3606 (class 2606 OID 16976)
-- Name: concept_orphan concept_orphan_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_orphan
    ADD CONSTRAINT concept_orphan_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 3580 (class 2606 OID 16978)
-- Name: concept concept_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept
    ADD CONSTRAINT concept_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 3608 (class 2606 OID 16980)
-- Name: concept_term_candidat concept_term_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.concept_term_candidat
    ADD CONSTRAINT concept_term_candidat_pkey PRIMARY KEY (id_concept, id_term, id_thesaurus);


--
-- TOC entry 3610 (class 2606 OID 16982)
-- Name: copyright copyright_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.copyright
    ADD CONSTRAINT copyright_pkey PRIMARY KEY (id_thesaurus);


--
-- TOC entry 3612 (class 2606 OID 16984)
-- Name: custom_concept_attribute custom_concept_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.custom_concept_attribute
    ADD CONSTRAINT custom_concept_attribute_pkey PRIMARY KEY ("idConcept");


--
-- TOC entry 3614 (class 2606 OID 16986)
-- Name: custom_term_attribute custom_term_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.custom_term_attribute
    ADD CONSTRAINT custom_term_attribute_pkey PRIMARY KEY (identifier);


--
-- TOC entry 3616 (class 2606 OID 16988)
-- Name: gps gps_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.gps
    ADD CONSTRAINT gps_pkey PRIMARY KEY (id_concept, id_theso);


--
-- TOC entry 3618 (class 2606 OID 16990)
-- Name: gps_preferences gps_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.gps_preferences
    ADD CONSTRAINT gps_preferences_pkey PRIMARY KEY (id_thesaurus, id_user, id_alignement_source);


--
-- TOC entry 3622 (class 2606 OID 16992)
-- Name: hierarchical_relationship_historique hierarchical_relationship_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.hierarchical_relationship_historique
    ADD CONSTRAINT hierarchical_relationship_copy_pkey PRIMARY KEY (id_concept1, id_thesaurus, role, id_concept2, modified, id_user);


--
-- TOC entry 3620 (class 2606 OID 16994)
-- Name: hierarchical_relationship hierarchical_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.hierarchical_relationship
    ADD CONSTRAINT hierarchical_relationship_pkey PRIMARY KEY (id_concept1, id_thesaurus, role, id_concept2);


--
-- TOC entry 3624 (class 2606 OID 16996)
-- Name: images images_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id_concept, id_thesaurus, image_name);


--
-- TOC entry 3626 (class 2606 OID 16998)
-- Name: info info_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.info
    ADD CONSTRAINT info_pkey PRIMARY KEY (version_opentheso, version_bdd);


--
-- TOC entry 3628 (class 2606 OID 17000)
-- Name: languages_iso639 languages_iso639_iso639_1_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.languages_iso639
    ADD CONSTRAINT languages_iso639_iso639_1_key UNIQUE (iso639_1);


--
-- TOC entry 3630 (class 2606 OID 17002)
-- Name: languages_iso639 languages_iso639_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.languages_iso639
    ADD CONSTRAINT languages_iso639_pkey PRIMARY KEY (id);


--
-- TOC entry 3632 (class 2606 OID 17004)
-- Name: node_label node_label_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.node_label
    ADD CONSTRAINT node_label_pkey PRIMARY KEY (facet_id, id_thesaurus, lang);


--
-- TOC entry 3635 (class 2606 OID 17006)
-- Name: non_preferred_term non_prefered_term_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.non_preferred_term
    ADD CONSTRAINT non_prefered_term_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus);


--
-- TOC entry 3638 (class 2606 OID 17008)
-- Name: non_preferred_term_historique non_preferred_term_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.non_preferred_term_historique
    ADD CONSTRAINT non_preferred_term_copy_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus, modified, id_user);


--
-- TOC entry 3647 (class 2606 OID 17010)
-- Name: note_historique note_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note_historique
    ADD CONSTRAINT note_copy_pkey PRIMARY KEY (id, modified, id_user);


--
-- TOC entry 3641 (class 2606 OID 17012)
-- Name: note note_notetypecode_id_thesaurus_id_concept_lang_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note
    ADD CONSTRAINT note_notetypecode_id_thesaurus_id_concept_lang_key UNIQUE (notetypecode, id_thesaurus, id_concept, lang, lexicalvalue);


--
-- TOC entry 3643 (class 2606 OID 17014)
-- Name: note note_notetypecode_id_thesaurus_id_term_lang_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note
    ADD CONSTRAINT note_notetypecode_id_thesaurus_id_term_lang_key UNIQUE (notetypecode, id_thesaurus, id_term, lang, lexicalvalue);


--
-- TOC entry 3645 (class 2606 OID 17016)
-- Name: note note_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note
    ADD CONSTRAINT note_pkey PRIMARY KEY (id);


--
-- TOC entry 3651 (class 2606 OID 17018)
-- Name: nt_type nt_type_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.nt_type
    ADD CONSTRAINT nt_type_pkey PRIMARY KEY (id);


--
-- TOC entry 3653 (class 2606 OID 17020)
-- Name: nt_type nt_type_relation_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.nt_type
    ADD CONSTRAINT nt_type_relation_key UNIQUE (relation);


--
-- TOC entry 3656 (class 2606 OID 17022)
-- Name: permuted permuted_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.permuted
    ADD CONSTRAINT permuted_pkey PRIMARY KEY (ord, id_concept, id_group, id_thesaurus, id_lang, lexical_value, ispreferredterm);


--
-- TOC entry 3649 (class 2606 OID 17024)
-- Name: note_type pk_note_type; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.note_type
    ADD CONSTRAINT pk_note_type PRIMARY KEY (code);


--
-- TOC entry 3670 (class 2606 OID 17026)
-- Name: relation_group pk_relation_group; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.relation_group
    ADD CONSTRAINT pk_relation_group PRIMARY KEY (id_group1, id_thesaurus, relation, id_group2);


--
-- TOC entry 3658 (class 2606 OID 17028)
-- Name: preferences preferences_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT preferences_id_thesaurus_key UNIQUE (id_thesaurus);


--
-- TOC entry 3660 (class 2606 OID 17030)
-- Name: preferences preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT preferences_pkey PRIMARY KEY (id_pref);


--
-- TOC entry 3662 (class 2606 OID 17032)
-- Name: preferences preferences_preferredname_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT preferences_preferredname_key UNIQUE (preferredname);


--
-- TOC entry 3664 (class 2606 OID 17034)
-- Name: preferences_sparql preferences_sparql_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferences_sparql
    ADD CONSTRAINT preferences_sparql_pkey PRIMARY KEY (thesaurus);


--
-- TOC entry 3666 (class 2606 OID 17036)
-- Name: preferred_term preferred_term_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.preferred_term
    ADD CONSTRAINT preferred_term_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 3668 (class 2606 OID 17038)
-- Name: proposition proposition_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.proposition
    ADD CONSTRAINT proposition_pkey PRIMARY KEY (id_concept, id_user, id_thesaurus);


--
-- TOC entry 3672 (class 2606 OID 17040)
-- Name: roles role_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3674 (class 2606 OID 17042)
-- Name: routine_mail routine_mail_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.routine_mail
    ADD CONSTRAINT routine_mail_pkey PRIMARY KEY (id_thesaurus);


--
-- TOC entry 3685 (class 2606 OID 17044)
-- Name: term_candidat term_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term_candidat
    ADD CONSTRAINT term_candidat_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus, contributor);


--
-- TOC entry 3688 (class 2606 OID 17046)
-- Name: term_historique term_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term_historique
    ADD CONSTRAINT term_copy_pkey PRIMARY KEY (id, modified, id_user);


--
-- TOC entry 3677 (class 2606 OID 17048)
-- Name: term term_id_term_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term
    ADD CONSTRAINT term_id_term_key UNIQUE (id_term, lang, id_thesaurus);


--
-- TOC entry 3679 (class 2606 OID 17050)
-- Name: term term_id_term_lexical_value_lang_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term
    ADD CONSTRAINT term_id_term_lexical_value_lang_id_thesaurus_key UNIQUE (id_term, lexical_value, lang, id_thesaurus);


--
-- TOC entry 3682 (class 2606 OID 17052)
-- Name: term term_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.term
    ADD CONSTRAINT term_pkey PRIMARY KEY (id);


--
-- TOC entry 3692 (class 2606 OID 17054)
-- Name: thesaurus_alignement_source thesaurus_alignement_source_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_alignement_source
    ADD CONSTRAINT thesaurus_alignement_source_pkey PRIMARY KEY (id_thesaurus, id_alignement_source);


--
-- TOC entry 3696 (class 2606 OID 17056)
-- Name: thesaurus_array_concept thesaurus_array_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_array_concept
    ADD CONSTRAINT thesaurus_array_concept_pkey PRIMARY KEY (thesaurusarrayid, id_concept, id_thesaurus);


--
-- TOC entry 3694 (class 2606 OID 17058)
-- Name: thesaurus_array thesaurus_array_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_array
    ADD CONSTRAINT thesaurus_array_pkey PRIMARY KEY (facet_id, id_thesaurus, id_concept_parent);


--
-- TOC entry 3698 (class 2606 OID 17060)
-- Name: thesaurus_label thesaurus_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_label
    ADD CONSTRAINT thesaurus_pkey PRIMARY KEY (id_thesaurus, lang, title);


--
-- TOC entry 3690 (class 2606 OID 17062)
-- Name: thesaurus thesaurus_pkey1; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus
    ADD CONSTRAINT thesaurus_pkey1 PRIMARY KEY (id_thesaurus, id_ark);


--
-- TOC entry 3700 (class 2606 OID 17064)
-- Name: thesaurus_label unique_thesau_lang; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.thesaurus_label
    ADD CONSTRAINT unique_thesau_lang UNIQUE (id_thesaurus, lang);


--
-- TOC entry 3702 (class 2606 OID 17066)
-- Name: user_group_label user_group-label_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_group_label
    ADD CONSTRAINT "user_group-label_pkey" PRIMARY KEY (id_group);


--
-- TOC entry 3704 (class 2606 OID 17068)
-- Name: user_group_thesaurus user_group_thesaurus_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_group_thesaurus
    ADD CONSTRAINT user_group_thesaurus_id_thesaurus_key UNIQUE (id_thesaurus);


--
-- TOC entry 3706 (class 2606 OID 17070)
-- Name: user_group_thesaurus user_group_thesaurus_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_group_thesaurus
    ADD CONSTRAINT user_group_thesaurus_pkey PRIMARY KEY (id_group, id_thesaurus);


--
-- TOC entry 3712 (class 2606 OID 17072)
-- Name: users user_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_pkey PRIMARY KEY (id_user);


--
-- TOC entry 3710 (class 2606 OID 17074)
-- Name: user_role_only_on user_role_only_on_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_role_only_on
    ADD CONSTRAINT user_role_only_on_pkey PRIMARY KEY (id_user, id_role, id_theso);


--
-- TOC entry 3708 (class 2606 OID 17076)
-- Name: user_role_group user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.user_role_group
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (id_user, id_role, id_group);


--
-- TOC entry 3724 (class 2606 OID 17078)
-- Name: users_historique users_historique_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users_historique
    ADD CONSTRAINT users_historique_pkey PRIMARY KEY (id_user);


--
-- TOC entry 3718 (class 2606 OID 17080)
-- Name: users2 users_login_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users2
    ADD CONSTRAINT users_login_key UNIQUE (login);


--
-- TOC entry 3720 (class 2606 OID 17082)
-- Name: users2 users_mail_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users2
    ADD CONSTRAINT users_mail_key UNIQUE (mail);


--
-- TOC entry 3714 (class 2606 OID 17084)
-- Name: users users_mail_key1; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_mail_key1 UNIQUE (mail);


--
-- TOC entry 3722 (class 2606 OID 17086)
-- Name: users2 users_pkey; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users2
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


--
-- TOC entry 3716 (class 2606 OID 17088)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 3578 (class 1259 OID 33967)
-- Name: concept_notation_unaccent; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX concept_notation_unaccent ON public.concept USING gin (public.f_unaccent(lower((notation)::text)) public.gin_trgm_ops);


--
-- TOC entry 3675 (class 1259 OID 17089)
-- Name: index_lexical_value; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX index_lexical_value ON public.term USING btree (lexical_value);


--
-- TOC entry 3686 (class 1259 OID 17090)
-- Name: index_lexical_value_copy; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX index_lexical_value_copy ON public.term_historique USING btree (lexical_value);


--
-- TOC entry 3633 (class 1259 OID 33838)
-- Name: index_lexical_value_npt; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX index_lexical_value_npt ON public.non_preferred_term USING btree (lexical_value);


--
-- TOC entry 3639 (class 1259 OID 33966)
-- Name: note_lexical_value_unaccent; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX note_lexical_value_unaccent ON public.note USING gin (public.f_unaccent(lower((lexicalvalue)::text)) public.gin_trgm_ops);


--
-- TOC entry 3654 (class 1259 OID 17091)
-- Name: permuted_lexical_value_idx; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX permuted_lexical_value_idx ON public.permuted USING btree (lexical_value);


--
-- TOC entry 3636 (class 1259 OID 33965)
-- Name: term_lexical_value_npt_unaccent; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX term_lexical_value_npt_unaccent ON public.non_preferred_term USING gin (public.f_unaccent(lower((lexical_value)::text)) public.gin_trgm_ops);


--
-- TOC entry 3680 (class 1259 OID 33964)
-- Name: term_lexical_value_unaccent; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX term_lexical_value_unaccent ON public.term USING gin (public.f_unaccent(lower((lexical_value)::text)) public.gin_trgm_ops);


--
-- TOC entry 3683 (class 1259 OID 33952)
-- Name: terms_values_gin; Type: INDEX; Schema: public; Owner: opentheso
--

CREATE INDEX terms_values_gin ON public.term USING gin (lexical_value public.gin_trgm_ops);


-- Completed on 2019-01-09 13:43:09 CET

--
-- PostgreSQL database dump complete
--

