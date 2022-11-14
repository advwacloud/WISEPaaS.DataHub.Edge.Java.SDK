## [1.0.6] - 2021-09-11

## [1.0.5] - 2021-09-11
### Update
- 取消100筆tag切分封包規則

## [1.0.4] - 2020-06-12
### Update
- 如果send data時的config在cache找不到, 還是給送

## [1.0.3] - 2020-06-12
### Add
- tag value型別檢查, 包含array tag
- 新增options.OS, 開發者需要指定OS為何, 如果為Android, 那pkg name為必填
    - 若非android, options.OS不用設定
- 新增MaxInflight, 預設10000, 用途為同時間publish thread的數量

### Update
- uploadConfig時, 取消與舊有config比對邏輯, 每次都publish mqtt msg
    - 會把新的config儲存下來(只實作在create和delsert)
- reconnect with dccs-refresh
    - init connect failure
    - connect lost failure
- 刪除NodeConfig
    - PrimaryIP
    - BackupIP
    - PrimaryPort
    - BackupPort
- 刪除DeviceConfig
    - ComPortNumber
    - IP
    - Port

## [1.0.2] - 2020-03-35
### Add
- 實作config cache機制
- 利用config cache實現FractionDisplayFormat預處理機制
- 紀錄上次的config, 如果config相同就不再傳送

### Fix
- 修正斷線後data recover每次只會緩存一個封包的問題

### Update
- remove deprecation properties: node's ID, name, and description
- support "Delsert" action of UploadConfig
- remove deprecation properties about alarm
- add RetentionPolicyName property of DeviceConfig
- dataRecoverTimer改成只在connected才啟動, disconnected時停止

## [1.0.1] - 2019-10-17
### Update
- Support android api22
- downgrade paho client to 1.2.0 to support android api22

## [1.0.0] - 2019-09-27
### Add
- sdk主要功能皆完成