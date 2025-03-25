<?php

namespace Database\Seeders;

use App\Models\Role;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

class RoleSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $timestamp = Carbon::now();

        Role::insert([
            ['name' => 'Admin', 'created_at' => $timestamp, 'updated_at' => $timestamp],
            ['name' => 'Client', 'created_at' => $timestamp, 'updated_at' => $timestamp]
        ]);
    }
}
