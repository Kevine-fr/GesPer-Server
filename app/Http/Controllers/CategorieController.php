<?php

namespace App\Http\Controllers;

use App\Models\Categorie;
use Illuminate\Http\Request;
use PhpParser\Node\Stmt\TryCatch;

class CategorieController extends Controller
{
    public function GetAllCategories()
    {
        try {
            $categories = Categorie::all();
            return response()->json(["message" => "Récupération des catégories avec succès !", "categories" => $categories], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "error" => $e->getMessage()], 500);
        }
    }

    public function GetCategories()
    {
        try {
            $categories = Categorie::limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "categories" => $categories], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "error" => $e->getMessage()], 500);
        }
    }
    public function CreateCategorie(Request $request)
    {
        try {
            $categorie = $request->validate([
                'title' => 'required|string',
                'subtitle' => 'required|string',
                'isOrganized' => 'boolean',
                'isSpent' => 'boolean',
            ]);

            $categorie = Categorie::create($categorie);
            return response()->json(["message" => "Catégorie créée avec succès !", "categorie" => $categorie], 201);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la création de la catégorie !", "error" => $e->getMessage()], 500);
        }
    }

    public function GetCategorie($categorieId)
    {
        try {
            $categorie = Categorie::find($categorieId);

            if(!$categorie) {
                return response()->json(["message" => "Catégorie introuvable !"], 404);
            }

            return response()->json(["message" => "Récupération de la catégorie avec succès !", "categorie" => $categorie], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération de la catégorie !", "error" => $e->getMessage()], 500);
        }
    }

    public function UpdateCategorie(Request $request, $categorieId)
    {
        try {
            $categorie = Categorie::find($categorieId);

            if(!$categorie) {
                return response()->json(["message" => "Catégorie introuvable !"], 404);
            }
            $validatedData = $request->validate([
                'title' => 'required|string',
                'subtitle' => 'required|string',
                'isOrganized' => 'boolean',
                'isSpent' => 'boolean',
            ]);

            $categorie->update($validatedData);
            
            return response()->json(["message" => "Catégorie modifiée avec succès !", "categorie" => $categorie], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la modification de la catégorie !", "error" => $e->getMessage()], 500);
        }
    }

    public function DeleteCategorie($categorieId)
    {
        try {
            $categorie = Categorie::find($categorieId);
            if(!$categorie) {
                return response()->json(["message" => "Catégorie introuvable !"], 404);
            }
            $categorie->delete();
            return response()->json(["message" => "Catégorie supprimée avec succès !"], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la suppression de la catégorie !", "error" => $e->getMessage()], 500);
        }
    }
}
