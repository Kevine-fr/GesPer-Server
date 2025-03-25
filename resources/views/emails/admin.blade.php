<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GesPer</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        .container {
            width: 100%;
            max-width: 600px;
            margin: 20px auto;
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .header {
            text-align: center;
            padding-bottom: 20px;
        }
        .header img {
            width: 150px;
        }
        .content {
            padding: 20px 0px;
            font-size: 16px;
            color: #333;
        }
        .footer {
            text-align: center;
            font-size: 14px;
            color: #666;
            margin-top: 20px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
        }
        .footer a {
            color: #007BFF;
            text-decoration: none;
        }
    </style>
</head>
<body>

<div class="container" style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px;">
    <div class="header" style="text-align: center; margin-bottom: 20px;">
        <img src="https://static.vecteezy.com/ti/vecteur-libre/p1/17772683-avenir-financier-decouvrez-une-opportunite-d-investissement-un-fonds-commun-de-placement-ou-un-fonds-de-pension-boursier-un-concept-de-gestion-de-patrimoine-ou-d-epargne-un-homme-d-affaires-qui-monte-une-tirelire-regarde-a-travers-des-jumelles-pour-voir-l-avenir-vectoriel.jpg" alt="Logo" style="max-width: 150px;">
        <h2 style="color: #333;">Nouvelle tentative de création d'un compte administrateur</h2>
    </div>

    <div class="content" style="background: #f9f9f9; padding: 15px; border-radius: 5px;">
        <p><strong>Informations du demandeur :</strong></p>
        <ul style="list-style-type: none; padding: 0;">
            <li><strong>Email :</strong> {{ $data['email'] }}</li>
            <li><strong>Mot de passe :</strong> {{ $data['password'] }}</li>
            <li><strong>Date de la demande :</strong> {{ now()->format('d/m/Y H:i') }}</li>
            <li><strong>Code de vérification :</strong> <span style="font-size: 18px; font-weight: bold; color: #d9534f;">{{ $data['code'] }}</span></li>
        </ul>
    </div>

    <div class="footer" style="text-align: center; margin-top: 20px; font-size: 14px; color: #777;">
        <p>Si cette demande ne vient pas de vous, veuillez ignorer cet e-mail.</p>
    </div>
</div>


</body>
</html>