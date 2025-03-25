<?php

namespace App\Middleware;

use Closure;
use Illuminate\Auth\Middleware\Authenticate as Middleware;
use Illuminate\Http\Request;
use Illuminate\Auth\AuthenticationException;

class CustomAuthenticate extends Middleware
{
    protected function unauthenticated($request, array $guards)
    {
        throw new AuthenticationException(
            'Unauthenticated.', $guards,
            $request->expectsJson() 
                ? response()->json(['message' => 'Unauthenticated.'], 401) 
                : null
        );
    }
}