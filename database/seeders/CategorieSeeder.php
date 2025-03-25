<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Carbon;

class CategorieSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = [
            // Dépenses
            ['title' => 'Logement', 'subtitle' => 'Loyer, Crédit immobilier, Charges', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Alimentation', 'subtitle' => 'Courses, Restaurants, Snacks', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Transport', 'subtitle' => 'Carburant, Assurance auto, Transport public, Entretien', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Santé', 'subtitle' => 'Mutuelle, Pharmacie, Consultations médicales', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Éducation', 'subtitle' => 'Frais de scolarité, Livres, Formations', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Divertissement & Loisirs', 'subtitle' => 'Cinéma, Abonnements, Voyages', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Factures & Services', 'subtitle' => 'Électricité, Internet, Téléphone, Eau', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Vêtements & Accessoires', 'subtitle' => 'Vêtements, Chaussures, Bijoux', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Dons & Cadeaux', 'subtitle' => 'Donations, Cadeaux d\'anniversaire', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Épargne & Investissement', 'subtitle' => 'Compte épargne, Bourse, Cryptos', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Impôts & Taxes', 'subtitle' => 'Impôt sur le revenu, Taxes locales', 'isOrganized' => true, 'isSpent' => true],
            ['title' => 'Divers', 'subtitle' => 'Dépenses imprévues, Autres achats', 'isOrganized' => true, 'isSpent' => true],
            
            // Gains
            ['title' => 'Salaire', 'subtitle' => 'Revenu principal, Primes, Bonus', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Revenus secondaires', 'subtitle' => 'Freelance, Travail à temps partiel', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Investissements', 'subtitle' => 'Dividendes, Intérêts bancaires, Crypto, Immobilier', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Aides & Allocations', 'subtitle' => 'Chômage, APL, Aides sociales', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Remboursements', 'subtitle' => 'Sécurité sociale, Assurance, Impôts', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Cadeaux & Dons reçus', 'subtitle' => 'Argent offert, Héritage, Parrainage', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Vente d’objets', 'subtitle' => 'Vente en ligne, Brocante, Matériel d’occasion', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Entrepreneuriat & Business', 'subtitle' => 'Revenus d’entreprise, E-commerce', 'isOrganized' => true, 'isSpent' => false],
            ['title' => 'Autres revenus', 'subtitle' => 'Sources diverses non catégorisées', 'isOrganized' => true, 'isSpent' => false],
        ];

        // Ajout des timestamps pour éviter les erreurs
        foreach ($categories as &$category) {
            $category['created_at'] = Carbon::now();
            $category['updated_at'] = Carbon::now();
        }

        DB::table('categories')->insert($categories);
    }
}
