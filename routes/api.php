<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\CategorieController;
use App\Http\Controllers\ClientController;
use App\Http\Controllers\GainController;
use App\Http\Controllers\SpentController;
use Illuminate\Http\Request;

Route::get('/', function () {
    return 'Hello world';
});

Route::get('/login', function () {
    return response()->json(['message' => "Vous n'êtes pas autorisé à éffectuer cette action !", 'errors' => 'Token invalide ou manquant'], 401);
})->name('login');

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

Route::controller(AuthController::class)->group(function () {
    Route::post('/register-admin', 'RegisterAdmin');
    Route::post('/login', 'Login');
    Route::post('/generate-code', 'GenerateCode');
    Route::post('/send-mail-admin', 'SendMailAdmin');

    Route::middleware('auth:sanctum')->group(function () {
        Route::get('/all-users', 'GetAllUsers');
        Route::get('/users', 'GetUsers');
        Route::get('/user/{userId}', 'GetUser');
        Route::get('/user-connected', 'GetUserConnected');
        Route::delete('/user-delete/{userId}', 'DeleteUser');
        Route::put('/user-update/{userId}', 'UpdateUser');
        Route::put('/user-update-connected', 'UpdateUserConnected');
        Route::put('/user-disable/{userId}', 'DisableUser');
        Route::put('/user-enable/{userId}', 'EnableUser');
        Route::post('/logout', 'Logout');
    });
});

Route::controller(ClientController::class)->group(function () {
    Route::post('/register-user', 'RegisterUser');
    Route::post('/send-mail-user', 'SendMailUser');
    Route::get('/test-access', 'TestAccess')->middleware('auth:sanctum');
});

Route::controller(CategorieController::class)->group(function () {
    Route::get('/get-all-categories', 'GetAllCategories');
    Route::get('/get-categories', 'GetCategories');
    Route::get('/get-categorie/{categorieId}', 'GetCategorie');
    Route::post('/create-categorie', 'CreateCategorie');
    Route::put('/update-categorie/{categorieId}', 'UpdateCategorie');
    Route::delete('/delete-categorie/{categorieId}', 'DeleteCategorie');
});

Route::middleware('auth:sanctum')->controller(GainController::class)->group(function () {
    Route::get('/get-all-gains', 'GetAllGains');
    Route::get('/get-gains', 'GetGains');
    Route::get('/get-all-gains-by-user', 'GetAllGainsByUser');
    Route::get('/get-gains-by-user', 'GetGainsByUser');
    Route::get('/get-gain-by-user/{gainId}', 'GetGainByUser');
    Route::get('/get-all-gains-by-user-for-admin/{gainId}', 'GetAllGainsByUserForAdmin');
    Route::get('/get-gains-by-user-for-admin/{gainId}', 'GetGainsByUserForAdmin');
    Route::get('/get-gain/{gainId}', 'GetGain');
    Route::post('/create-gain', 'CreateGain');
    Route::post('/create-gain-by-admin', 'CreateGainByAdmin');
    Route::put('/update-gain/{gainId}', 'UpdateGain');
    Route::put('/update-gain-by-user/{gainId}', 'UpdateGainByUser');
    Route::patch('/fake-delete-gain/{gainId}', 'FakeDeleteGain');
    Route::delete('/delete-gain/{gainId}', 'DeleteGain');
});

Route::middleware('auth:sanctum')->controller(SpentController::class)->group(function () {
    Route::get('/get-all-spents', 'GetAllSpents');
    Route::get('/get-spents', 'GetSpents');
    Route::get('/get-all-spents-by-user', 'GetAllSpentsByUser');
    Route::get('/get-spents-by-user', 'GetSpentsByUser');
    Route::get('/get-spent-by-user/{spentId}', 'GetSpentByUser');
    Route::get('/get-all-spents-by-user-for-admin/{spentId}', 'GetAllSpentsByUserForAdmin');
    Route::get('/get-spents-by-user-for-admin/{spentId}', 'GetSpentsByUserForAdmin');
    Route::get('/get-spent/{spentId}', 'GetSpent');
    Route::post('/create-spent', 'CreateSpent');
    Route::post('/create-spent-by-admin', 'CreateSpentByAdmin');
    Route::put('/update-spent/{spentId}', 'UpdateSpent');
    Route::put('/update-spent-by-user/{spentId}', 'UpdateSpentByUser');
    Route::patch('/fake-delete-spent/{spentId}', 'FakeDeleteSpent');
    Route::delete('/delete-spent/{spentId}', 'DeleteSpent');
});


