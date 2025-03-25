<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Categorie extends Model
{
    protected $fillable = [
        'title',
        'subtitle',
        'isOrganized',
        'isSpent'
    ];

    public function gains(): HasMany
    {
        return $this->hasMany(Gain::class);
    }

    public function spents(): HasMany
    {
        return $this->hasMany(Spent::class);
    }
}
