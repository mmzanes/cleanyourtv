# Abrir Vivo TV ao ligar (Fire TV)

Um app pequeno que abre o Vivo TV (`com.tid.enjoy.firetv`, o nome na Amazon Appstore) depois que o Fire TV liga. O Fire OS não deixa trocar a
tela inicial da Amazon, então este app não é um launcher: ele só coloca o Vivo TV na frente, uma vez por
inicialização completa (tirar da tomada ou reiniciar; sair do modo de espera pelo controle não conta),
depois que a rede conecta (ou após 45 segundos sem rede).

Testado para: Fire TV Stick 4K 2ª geração (Fire OS 8, Android 11). Precisa de Android 8 ou mais novo.

## Compilar

Precisa do JDK 17 e do Android SDK (plataforma 34). Nesta pasta:

```sh
./gradlew --no-daemon assembleRelease
```

O APK fica em `app/build/outputs/apk/release/app-release.apk`.

## Instalar

1. No Fire TV: Configurações → Minha Fire TV → Sobre → aperte OK 7 vezes no nome do aparelho.
2. Configurações → Minha Fire TV → Opções do desenvolvedor → Depuração ADB: ligado.
3. Anote o IP em Configurações → Minha Fire TV → Sobre → Rede.
4. No computador:

```sh
adb connect <ip da tv>:5555          # aceite o aviso na TV
adb shell pm list packages | grep -i enjoy   # confirme o nome do Vivo TV
adb install -r app/build/outputs/apk/release/app-release.apk
adb shell appops set io.github.mmzanes.bootopen SYSTEM_ALERT_WINDOW allow
adb shell am start -n io.github.mmzanes.bootopen/.MainActivity
```

O último comando abre o Vivo TV na hora (teste) e tira o app do estado "parado", sem o qual o Android não
entrega o aviso de inicialização. Depois reinicie: `adb reboot`.

Para testar o caminho da inicialização sem reiniciar (aperte Home antes, o Vivo TV deve abrir em ~5 s):

```sh
adb shell am broadcast -a io.github.mmzanes.bootopen.TEST -n io.github.mmzanes.bootopen/.BootReceiver
adb logcat -d -s BootOpen
```

Se o `grep` mostrar outro nome de pacote, troque `target_package` em `app/src/main/res/values/strings.xml`
e compile de novo.

5. Quando funcionar, desligue a Depuração ADB de novo (a porta 5555 não tem criptografia).

## Desfazer

```sh
adb uninstall io.github.mmzanes.bootopen
```
