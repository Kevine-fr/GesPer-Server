<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class Gain extends Model
{
    protected $fillable = [
        'user_id',
        'categorie_id',
        'libelle',
        'sum',
        'isReccurent',
        'isDeleted'
    ];
    
    public function spents(): HasMany
    {
        return $this->hasMany(Spent::class);
    }

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function categorie(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }
}
