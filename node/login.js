router.post('/login', (req, res)=>{
    const body = req.body;
    const id = body.id;
    const pw = body.pw;
    
    connection.query('select id, password from User where id=? and password=?', [id,pw], (err, data)=>{
      if(data.length == 0){ // 로그인 실패
        console.log('로그인 실패');
        res.status(200).json(
          {
            "UID" : -1
          }
        )
      }
      else{
        // 로그인 성공
        console.log('로그인 성공');
        connection.query('select UID from User where id=?',[id],(err,data)=>{
          res.status(200).send(data[0]); 
        });
        
      }
    });
  
  });

  router.post('/register', (req, res) =>{
    const body = req.body;
    const id = body.id;
    const pw = body.pw;
  
    connection.query('select * from User where id=?',[id],(err,data)=>{
      if(data.length == 0){
          console.log('회원가입 성공');
          connection.query('insert into User(id, password) values(?,?)',[id,pw]);
          res.status(200).json(
            {
              "message" : true
            }
          );
      }else{
          console.log('회원가입 실패');
          res.status(200).json(
            {
              "message" : false
            }
          );
          
      }
      
    });
  });

  router.get('/users_info', (req, res) => {
    connection.query('SELECT * FROM User', (error, rows) => {
      if(error) throw error;
      console.log('user info is : ', rows);
      
      res.status(200).send(rows)
      
    });
  });