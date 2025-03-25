<?php

namespace App\Http\Controllers;

use App\Models\Gain;
use App\Models\User;
use App\Models\Spent;
use App\Models\Categorie;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class SpentController extends Controller
{
    public function GetAllSpents()
    {
        try {
            $spents = Spent::all();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $spents], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetSpents()
    {
        try {
            $spents = Spent::limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $spents], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetAllSpentsByUser()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $spents = Spent::where('user_id', $user->id)->where('isDeleted', false)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $spents], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetSpentsByUser()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $spents = Spent::where('user_id', $user->id)->where('isDeleted', false)->limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $spents], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetAllSpentsByUserForAdmin()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            if (Auth::user()->role_id !== 1) {
                return response()->json(['message' => 'Accès refusé'], 403);
            }

            $spents = Spent::where('user_id', $user->id)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $spents], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetSpentsByUserForAdmin()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $spents = Spent::where('user_id', $user->id)->limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $spents], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function CreateSpent(Request $request)
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $validatedData = $request->validate([
                'user_id'      => 'nullable|integer|exists:users,id',
                'gain_id'      => 'nullable|integer|exists:gains,id',
                'categorie_id' => 'required|integer|exists:categories,id',
                'libelle'      => 'required|string|max:55',
                'value'        => 'required|numeric|min:0',
                'isSpent'      => 'required|boolean',
                'isDeleted'    => 'sometimes|boolean', 
            ]);
            
            $gain = $user->gains->where('id' , $validatedData['gain_id'])->where('isDeleted', false)->first();

            if(!$gain) {

                $spent = Spent::create( [
                    'user_id' => $user->id,
                    'gain_id' => null,
                    'categorie_id' => $validatedData['categorie_id'],
                    'libelle' => $validatedData['libelle'],
                    'value' => $validatedData['value'],
                    'isSpent' => $validatedData['isSpent'],
                    'isDeleted' => false
                ]);

                return response()->json([
                    "message" => "Dépense créé avec succès !",
                    "data" => $spent
                ], 201);
            }

            $spent = Spent::create( [
                'user_id' => $user->id,
                'gain_id' => $gain->id,
                'categorie_id' => $validatedData['categorie_id'],
                'libelle' => $validatedData['libelle'],
                'value' => $validatedData['value'],
                'isSpent' => $validatedData['isSpent'],
                'isDeleted' => false
            ]);

            return response()->json([
                "message" => "Dépense créé avec succès !",
                "data" => $spent
            ], 201);
        } 
        catch (\Exception $e) {
            return response()->json([
                "message" => "Erreur lors de la création de la dépense !",
                "errors" => $e->getMessage()
            ], 500);
        }
    }

    public function CreateSpentByAdmin(Request $request)
    {
        try {
            $validatedData = $request->validate([
                'user_id'   => 'required|integer|exists:users,id',
                'gain_id'      => 'nullable|integer|exists:gains,id',
                'categorie_id' => 'required|integer|exists:categories,id',
                'libelle'      => 'required|string|max:55',
                'value'        => 'required|numeric|min:0',
                'isSpent'      => 'required|boolean',
                'isDeleted'    => 'sometimes|boolean', 
            ]);

            $spent = Spent::create($validatedData);

            return response()->json([
                "message" => "Dépense créé avec succès !",
                "data" => $spent
            ], 201);
        }
        catch (\Exception $e) {
            return response()->json([
                "message" => "Erreur lors de la création de la dépense !",
                "errors" => $e->getMessage()
            ], 500);
        }
    }

    public function GetSpent($spentId)
    {
        try {
            $spent = Spent::find($spentId);

            if(!$spent) {
                return response()->json(["message" => "Dépense introuvable !"], 404);
            }

            return response()->json(["message" => "Récupération de la catégorie avec succès !", "data" => $spent], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération de la catégorie !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetSpentByUser($spentId)
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $spent = Spent::where('user_id', $user->id)->where('isDeleted', false)->find($spentId);

            if(!$spent) {
                return response()->json(["message" => "Dépense introuvable !"], 404);
            }

            return response()->json(["message" => "Récupération de la catégorie avec succès !", "data" => $spent], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération de la catégorie !", "errors" => $e->getMessage()], 500);
        }
    }

    public function UpdateSpentByUser(Request $request, $spentId)
    {
        try {
            $spent = Spent::where('id', $spentId)
                        ->where('user_id', Auth::id())
                        ->where('isDeleted', false)
                        ->first();

            if (!$spent) {
                return response()->json(["message" => "Dépense introuvable !"], 404);
            }

            $validatedData = $request->validate([
                'gain_id'      => 'nullable|integer|exists:gains,id',
                'categorie_id' => 'required|integer|exists:categories,id',
                'libelle'      => 'required|string|max:55',
                'value'        => 'required|numeric|min:0',
                'isSpent'      => 'required|boolean',
                'isDeleted'    => 'sometimes|boolean',
            ]);

            if (empty(array_diff_assoc($validatedData, $spent->toArray()))) {
                return response()->json(["message" => "Aucune modification n'a été effectuée !"], 409);
            }

            $spent->update($validatedData);

            return response()->json(["message" => "Dépense modifié avec succès !", "data" => $spent], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la modification de la dépense !", "errors" => $e->getMessage()], 500);
        }
    }


    public function UpdateSpent(Request $request, $spentId)
    {
        try {
            $spent = Spent::find($spentId);

            if(!$spent) {
                return response()->json(["message" => "Dépense introuvable !"], 404);
            }

            $validatedData = $request->validate([
                'user_id'   => 'required|integer|exists:users,id',
                'gain_id'      => 'nullable|integer|exists:gains,id',
                'categorie_id' => 'required|integer|exists:categories,id',
                'libelle'      => 'required|string|max:55',
                'value'        => 'required|numeric|min:0',
                'isSpent'      => 'required|boolean',
                'isDeleted'    => 'sometimes|boolean',
            ]);

            $spent->update($validatedData);

            return response()->json(["message" => "Dépense modifié avec succès !", "data" => $spent], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la modification de la dépense !", "errors" => $e->getMessage()], 500);
        }
    }

    public function FakeDeleteSpent(Request $request, $spentId)
    {
        try {
            $spent = Spent::where('id', $spentId)
                        ->where('user_id', Auth::id())
                        ->first();

            if (!$spent) {
                return response()->json(["message" => "Dépense introuvable !"], 404);
            }

            if ($spent->isDeleted) {
                return response()->json(["message" => "Cette dépense a déjà été supprimé !"], 409);
            }

            $spent->update(["isDeleted" => true]);

            return response()->json(["message" => "Dépense supprimé avec succès !", "data" => $spent], 200);
        } 
        catch (\Throwable $e) {
            return response()->json([
                "message" => "Erreur lors de la suppression de la dépense !",
                "errors" => $e->getMessage()
            ], 500);
        }
    }

    public function DeleteSpent($spentId)
    {
        try {
            $spent = Spent::find($spentId);
            if(!$spent) {
                return response()->json(["message" => "Dépense introuvable !"], 404);
            }
            $spent->delete();
            return response()->json(["message" => "Dépense supprimée avec succès !"], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la suppression de la catégorie !", "errors" => $e->getMessage()], 500);
        }
    }
}
