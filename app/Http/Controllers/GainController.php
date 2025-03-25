<?php

namespace App\Http\Controllers;

use App\Models\Gain;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class GainController extends Controller
{
    public function GetAllGains()
    {
        try {
            $gains = Gain::all();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $gains], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetGains()
    {
        try {
            $gains = Gain::limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $gains], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetAllGainsByUser()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $gains = Gain::where('user_id', $user->id)->where('isDeleted', false)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $gains], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetGainsByUser()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $gains = Gain::where('user_id', $user->id)->where('isDeleted', false)->limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $gains], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetAllGainsByUserForAdmin()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            if (Auth::user()->role_id !== 1) {
                return response()->json(['message' => 'Accès refusé'], 403);
            }

            $gains = Gain::where('user_id', $user->id)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $gains], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetGainsByUserForAdmin()
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $gains = Gain::where('user_id', $user->id)->limit(15)->get();
            return response()->json(["message" => "Récupération des catégories avec succès !", "data" => $gains], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération des catégories !", "errors" => $e->getMessage()], 500);
        }
    }

    public function CreateGain(Request $request)
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $validatedData = $request->validate([
                'categorie_id'=> 'required|integer|exists:categories,id',
                'libelle'     => 'required|string|max:55',
                'sum'         => 'required|numeric|min:0',
                'isReccurent' => 'required|boolean',
                'isDeleted'   => 'boolean',
            ]);

            $gain = Gain::create( [
                'user_id' => $user->id,
                'categorie_id' => $validatedData['categorie_id'],
                'libelle' => $validatedData['libelle'],
                'sum' => $validatedData['sum'],
                'isReccurent' => $validatedData['isReccurent'],
                'isDeleted' => false
            ]);

            return response()->json([
                "message" => "Gain créé avec succès !",
                "data" => $gain
            ], 201);
        } 
        catch (\Exception $e) {
            return response()->json([
                "message" => "Erreur lors de la création du gain !",
                "errors" => $e->getMessage()
            ], 500);
        }
    }

    public function CreateGainByAdmin(Request $request)
    {
        try {
            $validatedData = $request->validate([
                'user_id'   => 'required|integer|exists:users,id',
                'categorie_id'=> 'required|integer|exists:categories,id',
                'libelle'     => 'required|string|max:55',
                'sum'         => 'required|numeric|min:0',
                'isReccurent' => 'required|boolean',
                'isDeleted'   => 'boolean',
            ]);

            $gain = Gain::create($validatedData);

            return response()->json([
                "message" => "Gain créé avec succès !",
                "data" => $gain
            ], 201);
        } 
        catch (\Exception $e) {
            return response()->json([
                "message" => "Erreur lors de la création du gain !",
                "errors" => $e->getMessage()
            ], 500);
        }
    }

    public function GetGain($gainId)
    {
        try {
            $gain = Gain::find($gainId);

            if(!$gain) {
                return response()->json(["message" => "Gain introuvable !"], 404);
            }

            return response()->json(["message" => "Récupération de la catégorie avec succès !", "data" => $gain], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération de la catégorie !", "errors" => $e->getMessage()], 500);
        }
    }

    public function GetGainByUser($gainId)
    {
        try {
            $user = Auth::user();

            if(!$user) {
                return response()->json(["message" => "Impossible d'éffectuer cette action. Car votre compte est introuvable !"], 404);
            }

            $gain = Gain::where('user_id', $user->id)->where('isDeleted', false)->find($gainId);

            if(!$gain) {
                return response()->json(["message" => "Gain introuvable !"], 404);
            }

            return response()->json(["message" => "Récupération de la catégorie avec succès !", "data" => $gain], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la récupération de la catégorie !", "errors" => $e->getMessage()], 500);
        }
    }

    public function UpdateGainByUser(Request $request, $gainId)
    {
        try {
            $gain = Gain::where('id', $gainId)
                        ->where('user_id', Auth::id())
                        ->where('isDeleted', false)
                        ->first();

            if (!$gain) {
                return response()->json(["message" => "Gain introuvable !"], 404);
            }

            $validatedData = $request->validate([
                'categorie_id' => 'required|integer|exists:categories,id',
                'libelle'      => 'required|string|max:55',
                'sum'          => 'required|numeric|min:0',
                'isReccurent'  => 'required|boolean',
                'isDeleted'    => 'sometimes|boolean'
            ]);

            if (empty(array_diff_assoc($validatedData, $gain->toArray()))) {
                return response()->json(["message" => "Aucune modification n'a été effectuée !"], 409);
            }

            $gain->update($validatedData);

            return response()->json(["message" => "Gain modifié avec succès !", "data" => $gain], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la modification du gain !", "errors" => $e->getMessage()], 500);
        }
    }


    public function UpdateGain(Request $request, $gainId)
    {
        try {
            $gain = Gain::find($gainId);

            if(!$gain) {
                return response()->json(["message" => "Gain introuvable !"], 404);
            }

            $validatedData = $request->validate([
                'user_id'   => 'required|integer|exists:users,id',
                'categorie_id' => 'required|integer|exists:categories,id',
                'libelle'      => 'required|string|max:55',
                'sum'          => 'required|numeric|min:0',
                'isReccurent'  => 'required|boolean',
                'isDeleted'    => 'sometimes|boolean'
            ]);

            $gain->update($validatedData);

            return response()->json(["message" => "Gain modifié avec succès !", "data" => $gain], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la modification du gain !", "errors" => $e->getMessage()], 500);
        }
    }

    public function FakeDeleteGain(Request $request, $gainId)
{
    try {
        $gain = Gain::where('id', $gainId)
                    ->where('user_id', Auth::id())
                    ->first();

        if (!$gain) {
            return response()->json(["message" => "Gain introuvable !"], 404);
        }

        if ($gain->isDeleted) {
            return response()->json(["message" => "Ce gain a déjà été supprimé !"], 409);
        }

        $gain->update(["isDeleted" => true]);

        return response()->json(["message" => "Gain supprimé avec succès !", "data" => $gain], 200);
    } 
    catch (\Throwable $e) {
        return response()->json([
            "message" => "Erreur lors de la modification du gain !",
            "errors" => $e->getMessage()
        ], 500);
    }
}


    public function DeleteGain($gainId)
    {
        try {
            $gain = Gain::find($gainId);
            if(!$gain) {
                return response()->json(["message" => "Gain introuvable !"], 404);
            }
            $gain->delete();
            return response()->json(["message" => "Gain supprimée avec succès !"], 200);
        } 
        catch (\Exception $e) {
            return response()->json(["message" => "Erreur lors de la suppression de la catégorie !", "errors" => $e->getMessage()], 500);
        }
    }
}
