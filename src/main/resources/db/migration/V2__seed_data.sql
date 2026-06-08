-- Rôles
INSERT INTO roles (name, created_at, updated_at) VALUES
    ('ROLE_ADMIN',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ROLE_CLIENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Catégories de dépenses
INSERT INTO categories (title, subtitle, is_organized, is_spent, created_at, updated_at) VALUES
    ('Logement',               'Loyer, Crédit immobilier, Charges',                                    TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Alimentation',           'Courses, Restaurants, Snacks',                                          TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Transport',              'Carburant, Assurance auto, Transport public, Entretien',                TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Santé',                  'Mutuelle, Pharmacie, Consultations médicales',                          TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Éducation',              'Frais de scolarité, Livres, Formations',                                TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Divertissement & Loisirs','Cinéma, Abonnements, Voyages',                                         TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Factures & Services',    'Électricité, Internet, Téléphone, Eau',                                 TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Vêtements & Accessoires','Vêtements, Chaussures, Bijoux',                                         TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Dons & Cadeaux',         'Donations, Cadeaux d''anniversaire',                                    TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Épargne & Investissement','Compte épargne, Bourse, Cryptos',                                      TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Impôts & Taxes',         'Impôt sur le revenu, Taxes locales',                                    TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Divers',                 'Dépenses imprévues, Autres achats',                                     TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Catégories de gains
INSERT INTO categories (title, subtitle, is_organized, is_spent, created_at, updated_at) VALUES
    ('Salaire',                'Revenu principal, Primes, Bonus',                                       TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Revenus secondaires',    'Freelance, Travail à temps partiel',                                    TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Investissements',        'Dividendes, Intérêts bancaires, Crypto, Immobilier',                    TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Aides & Allocations',    'Chômage, APL, Aides sociales',                                          TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Remboursements',         'Sécurité sociale, Assurance, Impôts',                                   TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Cadeaux & Dons reçus',   'Argent offert, Héritage, Parrainage',                                   TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Vente d''objets',        'Vente en ligne, Brocante, Matériel d''occasion',                        TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Entrepreneuriat & Business','Revenus d''entreprise, E-commerce',                                  TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Autres revenus',         'Sources diverses non catégorisées',                                     TRUE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
