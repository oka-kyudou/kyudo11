# kyudoapp_source
アプリ『弓道部分析カメラ』のソースコード

＊コードが汚いのはご容赦ください

機械学習にはTensorFlow使っててデータベースはAndroidに組み込まれてるSQlite使ってます。
多分MySqlを実装しようとした形跡があると思いますが使ってません。（消すのがめんどかったっす）
あとvalue全然使ってないのは許してください…
asset内にあるバックアップファイルは実験用なので悪しからず。


一応さらっと構造だけ書いておきます。
jp.ac.kyudo
  -Camera(記録登録）
    -CameraActivity←DetectImage←TFModel(モデルはraw内)
    -EditAIResult(解析結果編集）
    -その他Adapter
  -Edit(記録編集）
    -Editrecord(Activity)(中身はEditAIresultとほぼ同じ）
    -Adapter
  -MainSelect
    -kyudoDBopenHelper(名前のまんま）（データ構造特殊だから後述）
    -SelectMenuActivity(ここにIntentFilterついてる）（起動画面）
    -TotalReport(起動画面のグラフを描くやつ）
    -prefences(設定）
  -Member(部員管理）
    -MemberJoinAll(入部）
    -MemberLeave(退部）（SQliteの仕様ですごいまどろっこしいことしてる）
  -Report（レポート）
    見ればわかる
  -Yumi（弓管理表）
    -YumiMain
    -Adapter
    
KyudoDBのデータ構造
kyudoDB
  -connection 使ってない（元々はサーバーが別にあり、そことの進捗共有等に使用）
  -hit_record 的中記録
    -hitID INT　立の管理番号　INT制約があるのでこれが保存できる上限になる　まぁ大丈夫だろう（フラグ）
    -date date そのまま
    - 0 INT　ゲストメンバー用のカラム初期設定時からずっといる
    - name_order 立順を登録しようと作ったものの使ってない
    - ここから部員の数だけ　INT でカラムが増えていく
    的中記録の表し方　基本はBITで考えた時、立っているビットの数が的中数となるが
    左から順に1、2、3、4本目を表すので注意！
    Ex）　一本目と4本目が当たった場合→1001＝9となる
    （レポートのどこかで間違って解釈していて対症療法的に直したとこあるから気をつけて）
  - member_list（部員名簿）
    -  memberID 入った西暦＋通し番号　Ex)2020年に入学し、一番最初に登録した池田くん→202001
    -  family_name
    -  first_name
    -  sex 性別　0-男 1-女 2-その他
  -yumi_list　弓管理表
    - yumiID 管理番号　ゆみに貼ってある数字と一緒
    - kind 弓の種類　0-直心　1ー練心　2ーその他
    - weight　弓力　FLOAT
    - nobi 0-並寸　1ー伸寸
    - cation　備考欄
  -yumi_user 弓と持ち主の対応表
    - memberID
    - yumiID
まぁこんな感じです。これだけ見てもわかるようにさまざまな仕様上の欠陥があります。もし修正してくださる人がいるなら、是非よろしくお願いします。
    
