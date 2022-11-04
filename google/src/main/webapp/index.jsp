<html>
    <head>
        <title>Google SSO Test</title>
        <style>
        .chip {
          display: inline-block;
          padding: 0 25px;
          height: 50px;
          font-size: 16px;
          line-height: 50px;
          border-radius: 25px;
          background-color: #f1f1f1;
        }

        .chip img {
          float: left;
          margin: 0 10px 0 -25px;
          height: 50px;
          width: 50px;
          border-radius: 25%;
        }
        </style>
    </head>
    <body>
        Welcome
        <div class="chip">
          <img src="${userinfo.picture}" alt="Person" width="96" height="96">
          ${userinfo.name}
        </div>
    </body>
</html>