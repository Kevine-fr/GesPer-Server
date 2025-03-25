<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Mail;
use Illuminate\Http\Request;
use App\Mail\AdminMail;
use App\Models\User;

class AuthController extends Controller
{
    public function GenerateCode(){
        $code = random_int(100000, 999999);
        return $code;
    }
    public function RegisterAdmin(Request $request){
        try {
            $request->validate([
                'name' => 'required|string|max:255',
                'email' => 'required|string|email|max:255|unique:users',
                'password' => 'required|string|min:8',
            ]);

            User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),
                'role_id' => 1
            ]);

            return response()->json(['message' => 'Admin crée avec succès !'], 201);
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue lors de la création de l'Admin. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
        }
    }

    public function SendMailAdmin(Request $request)
    {
        try {
            $data = $request->validate([
                'email' => 'required|string|email|max:255|unique:users',
                'password' => 'required|string|min:8'
            ]);
    
            $code = $this->GenerateCode();
            $data['code'] = $code;
            $email = $data['email'];

            $subject = "Cet email : $email, tente de s'inscrire en tant qu'administrateur !";

            if (Cache::has("email_sent_$email")) {
                return response()->json(['message' => 'Veuillez patienter 15 secondes avant de réessayer.'], 429);
            }
    
            Cache::put("email_sent_$email", true, now()->addSeconds(15));

            Mail::to(env('MAIL_USERNAME') ?? 'keddiantouadi@gmail.com')->send(new AdminMail($data, $subject));

            return response()->json(['message' => 'E-mail envoyé avec succès !'] , 200);

        } catch (\Exception $e) {
            return response()->json(["message" => "Échec de l'envoi. Veuillez vérifier votre connexion !", "errors" => $e->getMessage()], 500);
        }
    }

    public function Login(Request $request)
    {
        try {
            $request->validate([
                'email' => 'required|string|email',
                'password' => 'required|string',
            ]);

            $credentials = $request->only('email', 'password');

            if (!Auth::attempt($credentials)) {
                return response()->json(['message' => 'Email ou mot de passe incorrect !'], 401);
            }

            $user = Auth::user();

            if ($user->client && !$user->client->isActif && $user->role_id !== 1) {
                return response()->json(['message' => "Votre compte a été banni !"], 403);
            }

            $token = $user->createToken('auth_token')->plainTextToken;

            return response()->json([
                'token' => $token
            ]);
        } catch (\Exception $e) {
            return response()->json([
                'message' => 'Une erreur est survenue lors de la connexion. Veuillez réessayer !',
                'errors' => $e->getMessage()
            ], 500);
        }
    }

    public function Logout(Request $request)
    {
        try {
            $request->user()->currentAccessToken()->delete();
            return response()->json(['message' => 'Déconnexion réussie !']);
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue lors de la déconnexion. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
        }
    }

    public function GetAllUsers()
    {
        if (Auth::user() && Auth::user()->role_id !== 1) {
            return response()->json(['message' => 'Accès refusé !'], 403);
        }

        try {
            $user = User::all();
            return response()->json(['message' => "Récupération éffectué avec succès !" , 'data' =>  $user]);
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue lors de la récupération. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
        }
    }

    public function GetUsers()
{

    if (Auth::user() && Auth::user()->role_id !== 1) {
        return response()->json(['message' => 'Accès refusé !'], 403);
    }

    try {
        $users = User::limit(15)->get();
        return response()->json(['message' => "Récupération éffectué avec succès !" , 'data' =>  $users]);
    } catch (\Exception $e) {
        return response()->json(['message' => "Une erreur est survenue lors de la récupération. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
    }
}

    public function GetUser($userId)
    {

        if (Auth::user() && Auth::user()->role_id !== 1) {
            return response()->json(['message' => 'Accès refusé !'], 403);
        }

        try {
            $user = User::find($userId);

            if (!$user) {
                return response()->json(['message' => "Cet utilisateur n'existe pas !"], 404);
            }

            return response()->json(['message' => "Récupération éffectué avec succès !" , 'data' =>  $user]);
            
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue lors de la récupération. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
        }
    }

    public function GetUserConnected()
    {
        try {
            $idUserConnected = Auth::user()->id;
            $user = User::find($idUserConnected);

            if (!$user) {
                return response()->json(['message' => "Cet utilisateur n'existe pas !"], 404);
            }

            return response()->json(['message' => "Récupération éffectué avec succès !" , 'data' =>  $user]);
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue lors de la récupération. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
        }
    }

    public function DeleteUser($userId)
    {

        if (Auth::user() && Auth::user()->role_id !== 1) {
            return response()->json(['message' => 'Accès refusé !'], 403);
        }

        try {
            $user = User::find($userId);

            if (!$user) {
                return response()->json(['message' => "Cet utilisateur n'existe pas !"], 404);
            }

            $user->delete();

            return response()->json(['message' => "Suppression éffectué avec succès !"]);
        } catch (\Exception $e) {
            return response()->json(['message' => "Une erreur est survenue lors de la suppression. Veuillez réessayer !" , 'errors' => $e->getMessage()], 500);
        }
    }

    public function UpdateUser(Request $request, $userId)
    {
        if (Auth::user() && Auth::user()->role_id !== 1) {
            return response()->json(['message' => 'Accès refusé !'], 403);
        }

        try {
            $user = User::find($userId);

            if (!$user) {
                return response()->json(['message' => "Cet utilisateur n'existe pas !"], 404);
            }

            $validatedData = $request->validate([
                'name' => 'sometimes|string|max:255',
                'email' => 'sometimes|email|unique:users,email,' . $userId,
                'password' => 'sometimes|string|min:6'
            ]);

            if ($request->filled('password')) {
                $validatedData['password'] = bcrypt($validatedData['password']);
            }

            $user->update(array_filter($validatedData));

            return response()->json([
                'message' => "Utilisateur mis à jour avec succès !",
                'data' => $user
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'message' => "Une erreur est survenue lors de la mise à jour. Veuillez réessayer !",
                'errors' => $e->getMessage()
            ], 500);
        }
    }

    public function UpdateUserConnected(Request $request)
    {
        if (!Auth::user()) {
            return response()->json(['message' => 'Echec de la modification ! Vous n\'êtes pas connecté.'], 404);
        }

        try {
            $user = Auth::user();

            $validatedData = $request->validate([
                'name' => 'sometimes|string|max:255',
                'email' => 'sometimes|email|unique:users,email,' . $user->id,
                'password' => 'sometimes|string|min:6'
            ]);

            if ($request->filled('password')) {
                $validatedData['password'] = bcrypt($validatedData['password']);
            }

            $user->update(array_filter($validatedData));

            return response()->json([
                'message' => "Utilisateur mis à jour avec succès !",
                'data' => $user
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'message' => "Une erreur est survenue lors de la mise à jour. Veuillez réessayer !",
                'errors' => $e->getMessage()
            ], 500);
        }
    }

    public function DisableUser($userId)
    {
        if (Auth::user() && Auth::user()->role_id !== 1) {
            return response()->json(['message' => 'Accès refusé !'], 403);
        }

        try {
            $user = User::find($userId);

            if (!$user) {
                return response()->json(['message' => "Cet utilisateur n'existe pas !"], 404);
            }

            if (!$user->client) {
                return response()->json(['message' => "Ce client n'existe pas !"], 404);
            }

            if (!$user->client->isActif) {
                return response()->json(['message' => "Ce client est déjà banni !"], 409);
            }

            $user->client->update(['isActif' => false]);

            $user->tokens()->delete();

            return response()->json(['message' => "Désactivation effectuée avec succès !"]);
        } catch (\Exception $e) {
            return response()->json([
                'message' => "Une erreur est survenue lors de la désactivation. Veuillez réessayer !",
                'errors' => $e->getMessage()
            ], 500);
        }
    }

    public function EnableUser($userId)
    {
        if (Auth::user() && Auth::user()->role_id !== 1) {
            return response()->json(['message' => 'Accès refusé !'], 403);
        }

        try {
            $user = User::find($userId);

            if (!$user) {
                return response()->json(['message' => "Cet utilisateur n'existe pas !"], 404);
            }

            if (!$user->client) {
                return response()->json(['message' => "Ce client n'existe pas !"], 404);
            }

            if ($user->client->isActif) {
                return response()->json(['message' => "Ce client est déjà actif !"], 409);
            }

            $user->client->update(['isActif' => true]);

            return response()->json(['message' => "Activation effectuée avec succès !"]);
        } catch (\Exception $e) {
            return response()->json([
                'message' => "Une erreur est survenue lors de l'activation. Veuillez réessayer !",
                'errors' => $e->getMessage()
            ], 500);
        }
    }
}
