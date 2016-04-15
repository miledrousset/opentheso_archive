-- * Author:  miled.rousset
-- * Created: 15 avr. 2016
-- */


-- # !!!!!!! Attention !!!!!!! opération irréversible
-- # Opération à appliquer pour l'installation et la préparation de la BDD  

-- # Suppression de tous les thésaurus et des données
delete from alignement;
delete from concept;
delete from concept_candidat;
delete from concept_fusion;
delete from concept_group;
delete from concept_group_concept;
delete from concept_group_historique;
delete from concept_group_label;
delete from concept_group_label_historique;
delete from concept_historique;
delete from concept_orphan;
delete from concept_term_candidat;
delete from hierarchical_relationship;
delete from hierarchical_relationship_historique;
delete from images;
delete from non_preferred_term;
delete from non_preferred_term_historique;
delete from note;
delete from note_historique;
delete from permuted;
delete from preferences;
delete from preferred_term;
delete from proposition;
delete from term;
delete from term_candidat;
delete from term_historique;
delete from thesaurus;
delete from thesaurus_array;
delete from thesaurus_label;
delete from user_role;
delete from users;
delete from thesaurus_array_concept;



-- # initialisation des séquences 
ALTER SEQUENCE alignement_id_seq RESTART WITH 1;
ALTER SEQUENCE concept__id_seq RESTART WITH 1;
ALTER SEQUENCE concept_candidat__id_seq RESTART WITH 1;
ALTER SEQUENCE concept_group__id_seq RESTART WITH 1;
ALTER SEQUENCE concept_group_label_id_seq RESTART WITH 1;
ALTER SEQUENCE definition_note__id_seq RESTART WITH 1;
ALTER SEQUENCE editorial_note__id_seq RESTART WITH 1;
ALTER SEQUENCE facet_id_seq RESTART WITH 1;
ALTER SEQUENCE history_note__id_seq RESTART WITH 1;
ALTER SEQUENCE note__id_seq RESTART WITH 1;
ALTER SEQUENCE pref__id_seq RESTART WITH 1;
ALTER SEQUENCE term__id_seq RESTART WITH 1;
ALTER SEQUENCE term_candidat__id_seq RESTART WITH 1;
ALTER SEQUENCE thesaurus_id_seq RESTART WITH 1;
ALTER SEQUENCE user__id_seq RESTART WITH 2;

-- # créarion de l'admin pour la première connexion
INSERT INTO users (id_user, username, password, active, mail) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', true, 'admin@domaine.fr');
INSERT INTO user_role (id_user, id_role, id_thesaurus, id_group) VALUES (1, 1, '','');