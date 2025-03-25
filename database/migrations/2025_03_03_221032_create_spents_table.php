<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('spents', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade')->onUpdate('cascade');
            $table->foreignId('gain_id')->nullable()->constrained('gains')->onDelete('cascade')->onUpdate('cascade');
            $table->foreignId('categorie_id')->nullable()->constrained('categories')->onDelete('cascade')->onUpdate('cascade');
            $table->string('libelle')->nullable();
            $table->boolean('isSpent')->default(true);
            $table->float('value');
            $table->boolean('isDeleted')->default(false);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('spents');
    }
};
