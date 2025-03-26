<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Auth;
use Illuminate\Http\Request;
use App\Mail\ClientMail;
use App\Models\Client;
use App\Models\User;
use Illuminate\Support\Facades\DB;

class ClientController extends Controller
{   
    public function GenerateCode(){
        try {    
            $code = random_int(100000, 999999);
            
            return $code;
        } catch (\Throwable $th) {
            return response()->json(['message' => 'Une erreur est survenue lors de la génération du code. Veuillez réessayer !', 'errors' => $th->getMessage()], 500);
        }
    }

    public function VerificationDB(){
        try {
            DB::connection()->getPdo(); 
        } catch (\Exception $e) {
            return response()->json([
                'message' => 'Connexion à la base de données impossible.',
                'errors' => $e->getMessage()
            ], 500);
        }
    }

    public function VerificationAccess()
    {
        try {
            $this->VerificationDB();
            
            $user = Auth::user();

            if (!$user) {
                return response()->json(['message' => 'Accès refusé ! Vous n\'êtes pas authentifié.'], 403);
            }

            if ($user->role_id !== 1) { 
                if (!$user->client || !$user->client->isActif) {
                    return response()->json(['message' => 'Accès refusé ! Votre compte est désactivé.'], 403);
                }
            }
            return null; 
        } catch (\Throwable $th) {
            return response()->json(['message' => 'Une erreur est survenue. Veuillez réessayer !', 'errors' => $th->getMessage()], 500);
        }
    }

    public function TestAccess()
    {
        try {
            $accessDenied = $this->VerificationAccess();
            if ($accessDenied) {
                return $accessDenied; 
            }
            return response()->json(['message' => "Tu es autorisé ! 🎉"]);
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue. Veuillez réessayer !", 'errors' => $e->getMessage()], 500);
        }
    }

    public function RegisterUser(Request $request){
        try {
            $request->validate([
                'name' => 'required|string|max:255',
                'email' => 'required|string|email|max:255|unique:users',
                'password' => 'required|string|min:8',
            ]);

            $user = User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),
                'role_id' => 2
            ]);

            Client::create([
                'user_id' => $user->id,
                'isActif' => true
            ]);

            return response()->json(['message' => 'Compte crée avec succès !'], 201);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Une erreur est survenue lors de la création de compte. Veuillez réessayer !' , 'errors' => $e->getMessage()], 500);
        }
    }

    public function SendMailUser(Request $request)
    {
        try {
            $data = $request->validate([
                'email' => 'required|email',
                'password' => 'string|min:8',
            ]);
    
            $email = $data['email'];

            $code = $this->GenerateCode();
            $data['code'] = $code;
            $email = $data['email'];

            $subject = "Confirmation d'inscription via code de vérification";

            Mail::to($email)->send(new ClientMail($data, $subject));

            return response()->json(['message' => 'E-mail envoyé avec succès !'] , 200);

        } catch (\Exception $e) {
            return response()->json(["message" => "Échec de l'envoi. Veuillez vérifier votre connexion !", "errors" => $e->getMessage()], 500);
        }
    }
}
