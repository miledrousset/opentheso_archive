
-- Mise à jour de la table de types d'alignement
DROP TABLE alignement_type;

CREATE TABLE alignement_type
(
  id integer NOT NULL,
  label text NOT NULL,
  isocode text NOT NULL,
  label_skos character varying,
  CONSTRAINT alignment_type_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alignement_type
  OWNER TO opentheso;

INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (1, 'Equivalence exacte', '=EQ', 'exactMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (2, 'Equivalence inexacte', '~EQ', 'closeMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (3, 'Equivalence générique', 'EQB', 'broadMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (4, 'Equivalence associative', 'EQR', 'relatedMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (5, 'Equivalence spécifique', 'EQS', 'narrowMatch');



-- Mise à jour de la Function: unaccent_string(text)

DROP FUNCTION unaccent_string(text);

CREATE OR REPLACE FUNCTION unaccent_string(text)
  RETURNS text AS
$BODY$
DECLARE
input_string text := $1;
BEGIN

input_string := translate(input_string, 'âãäåāăąÁÂÃÄÅĀĂĄ', 'aaaaaaaaaaaaaaa');
input_string := translate(input_string, 'èééêëēĕėęěĒĔĖĘĚ', 'eeeeeeeeeeeeeee');
input_string := translate(input_string, 'ìíîïìĩīĭÌÍÎÏÌĨĪĬ', 'iiiiiiiiiiiiiiii');
input_string := translate(input_string, 'óôõöōŏőÒÓÔÕÖŌŎŐ', 'ooooooooooooooo');
input_string := translate(input_string, 'ùúûüũūŭůÙÚÛÜŨŪŬŮ', 'uuuuuuuuuuuuuuuu');
input_string := translate(input_string, '-_/()', '     ');

return input_string;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION unaccent_string(text)
  OWNER TO opentheso;



--mise à jour de la table note_type
DROP TABLE note_type;

CREATE TABLE note_type
(
  code text NOT NULL,
  isterm boolean NOT NULL,
  isconcept boolean NOT NULL,
  CONSTRAINT pk_note_type PRIMARY KEY (code),
  CONSTRAINT chk_not_false_values CHECK (NOT (isterm = false AND isconcept = false))
)
WITH (
  OIDS=FALSE
);
ALTER TABLE note_type
  OWNER TO opentheso;

INSERT INTO note_type (code, isterm, isconcept) VALUES ('customNote', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('definition', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('editorialNote', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('historyNote', true, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('scopeNote', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('note', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('example', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('changeNote', true, false);


--mise à jour de la table term 
ALTER TABLE term ADD COLUMN contributor character varying;
ALTER TABLE term ADD COLUMN creator character varying;


-- Table: preferences

DROP TABLE preferences;

CREATE TABLE preferences
(
  id_pref integer NOT NULL,
  source_lang character varying(3),
  nb_alert_cdt integer,
  alert_cdt boolean,
  id_thesaurus character varying NOT NULL,
  CONSTRAINT preferences_pkey PRIMARY KEY (id_pref)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE preferences
  OWNER TO opentheso;

INSERT INTO preferences (id_pref, source_lang, nb_alert_cdt, alert_cdt, id_thesaurus) VALUES (1, 'fr', 5, false, '1');


--
-- TOC entry 210 (class 1259 OID 56478)
-- Name: roles; Type: TABLE; Schema: public; Owner: opentheso; Tablespace: 
--
DROP TABLE roles;

CREATE TABLE roles (
    id integer NOT NULL,
    name character varying,
    description character varying
);


ALTER TABLE roles OWNER TO opentheso;

--
-- TOC entry 211 (class 1259 OID 56484)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: opentheso
--

CREATE SEQUENCE role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE role_id_seq OWNER TO opentheso;

--
-- TOC entry 2465 (class 0 OID 0)
-- Dependencies: 211
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: opentheso
--

ALTER SEQUENCE role_id_seq OWNED BY roles.id;


--
-- TOC entry 2347 (class 2604 OID 56571)
-- Name: id; Type: DEFAULT; Schema: public; Owner: opentheso
--

ALTER TABLE ONLY roles ALTER COLUMN id SET DEFAULT nextval('role_id_seq'::regclass);


--
-- TOC entry 2466 (class 0 OID 0)
-- Dependencies: 211
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: opentheso
--

SELECT pg_catalog.setval('role_id_seq', 1, false);


--
-- TOC entry 2459 (class 0 OID 56478)
-- Dependencies: 210
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: opentheso
--

INSERT INTO roles (id, name, description) VALUES (1, 'superAdmin', 'Super Administrateur pour tout gérer tout thésaurus et tout utilisateur');
INSERT INTO roles (id, name, description) VALUES (2, 'admin', 'administrateur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (3, 'user', 'utilisateur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (4, 'traducteur', 'traducteur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (5, 'images', 'gestion des images par thésaurus ou plus');
ALTER TABLE ONLY roles
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


