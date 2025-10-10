package com.github.gv2011.util.sec;

import static com.github.gv2011.testutil.Assert.assertThat;
import static com.github.gv2011.testutil.Matchers.hasSize;
import static com.github.gv2011.testutil.Matchers.is;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.security.cert.X509Certificate;

import org.junit.Test;

import com.github.gv2011.testutil.AbstractTest;

public class SecUtilsTest extends AbstractTest{

  @Test
  public void testCreateJKSKeyStore() {
    final RsaKeyPair privKey = RsaKeyPair.parsePkcs8(getResourceBytes("rsaprivcrt.pkcs8"));
    final X509Certificate cert = SecUtils.readCertificate(getResourceBytes("cert.der"));
    SecUtils.createJKSKeyStoreBytes(privKey, SecUtils.createCertificateChain(listOf(cert)));
  }

  @Test
  public void testReadCertificateFromPem() {
    final CertificateChain chain = SecUtils.readCertificateChainFromPem(CERT_1+CERT_2);
    assertThat(chain.certificates(), hasSize(2));
    assertThat(chain.leafCertificate().getSubjectX500Principal().getName("CANONICAL"), is("cn=topten.letero.com"));
    assertThat(
      chain.certificates().get(1).getSubjectX500Principal().getName("CANONICAL"),
      is("cn=let's encrypt authority x3,o=let's encrypt,c=us")
    );
  }

//TODO:fix
//  @Test
//  public void testCreateServerSocketFactory() throws IOException {
//    final SSLServerSocketFactory ssf = SecUtils.createServerSocketFactory(testFolder());
//    try(final ServerSocket ss = ssf.createServerSocket()){
//      ss.bind(null);
//      final int port = ss.getLocalPort();
//      log.info("Port: {}", port);
//      try(final CloseableFuture<Nothing> task =
//        ExecutorUtils.callAsync(()->{
//          Socket s = (SSLSocket) ss.accept();
//          int i = s.getInputStream().read();
//          verifyEqual(i, 3);
//          return nothing();
//        })
//      ){
//        try(final Socket s = (SSLSocket)
//          SecUtils.createSocketFactory(testFolder())
//          .createSocket(InetAddress.getLoopbackAddress(), port)
//        ){
//          final OutputStream out = s.getOutputStream();
//          out.write(3);
//          out.flush();
//        }
//      }
//    }
//  }


  private static final String CERT_1 =
      "-----BEGIN CERTIFICATE-----\n" +
      "MIIFBjCCA+6gAwIBAgISA9cfiZkhbXsMG0qeXZpYMac9MA0GCSqGSIb3DQEBCwUA\n" +
      "MEoxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1MZXQncyBFbmNyeXB0MSMwIQYDVQQD\n" +
      "ExpMZXQncyBFbmNyeXB0IEF1dGhvcml0eSBYMzAeFw0xNzA0MTQxMzMzMDBaFw0x\n" +
      "NzA3MTMxMzMzMDBaMBwxGjAYBgNVBAMTEXRvcHRlbi5sZXRlcm8uY29tMIIBIjAN\n" +
      "BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiJKDbIvpnpiCQ8uuEcsnl32n7Mau\n" +
      "FN8EYH2EMFyvl7PR80YommcjWn6eA/2NC9G8ZJFs7k6klCCWFoFUQCRqxqllQp4h\n" +
      "783v5lLhz+878UbU+ngydklVyuixXR4tbG/ov5YQBfzsHS1J9+o1sVKhJ+rcEXJP\n" +
      "IJFlHrnXMm64xGw9zLfPECsfAiErRNjyKYDQm+JloP5jUR4gZvQnT7OHOttYm1sH\n" +
      "N1vStNunq5Z0C2QcA5XbUm1tvMWuc2Kzr1wrcuC8WnBnoLTUxAVm5q+pY5o3dS6z\n" +
      "9LsWgShtoUUSaOydM5yz4GAsm9ft3EdQ15I0AAWaTjtJqIjjrWh2+GoL/QIDAQAB\n" +
      "o4ICEjCCAg4wDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggr\n" +
      "BgEFBQcDAjAMBgNVHRMBAf8EAjAAMB0GA1UdDgQWBBQBNjkVBgNY9t7sJE+q/8+b\n" +
      "P9mJMTAfBgNVHSMEGDAWgBSoSmpjBH3duubRObemRWXv86jsoTBwBggrBgEFBQcB\n" +
      "AQRkMGIwLwYIKwYBBQUHMAGGI2h0dHA6Ly9vY3NwLmludC14My5sZXRzZW5jcnlw\n" +
      "dC5vcmcvMC8GCCsGAQUFBzAChiNodHRwOi8vY2VydC5pbnQteDMubGV0c2VuY3J5\n" +
      "cHQub3JnLzAcBgNVHREEFTATghF0b3B0ZW4ubGV0ZXJvLmNvbTCB/gYDVR0gBIH2\n" +
      "MIHzMAgGBmeBDAECATCB5gYLKwYBBAGC3xMBAQEwgdYwJgYIKwYBBQUHAgEWGmh0\n" +
      "dHA6Ly9jcHMubGV0c2VuY3J5cHQub3JnMIGrBggrBgEFBQcCAjCBngyBm1RoaXMg\n" +
      "Q2VydGlmaWNhdGUgbWF5IG9ubHkgYmUgcmVsaWVkIHVwb24gYnkgUmVseWluZyBQ\n" +
      "YXJ0aWVzIGFuZCBvbmx5IGluIGFjY29yZGFuY2Ugd2l0aCB0aGUgQ2VydGlmaWNh\n" +
      "dGUgUG9saWN5IGZvdW5kIGF0IGh0dHBzOi8vbGV0c2VuY3J5cHQub3JnL3JlcG9z\n" +
      "aXRvcnkvMA0GCSqGSIb3DQEBCwUAA4IBAQBNZmKLTe5rRbrJwc0zd2ME/jIvcR+x\n" +
      "d8J9PxgKbSruSPuIZuhBxb9NYWR5swJ4ZKi5bct6wQ/lwPNXWzpl/x8b1dZOqUnh\n" +
      "DSD0CSaxJpViXzbMFIpESsGYZrFr9tVz0fFqrrshjIU2nM6KWr6MFP3Dd1Fp+oya\n" +
      "qin10A+2cqNeJXXVMIjx1xPuxZLkbhvz4r6i88mJULrJeoyc/mfKGG3NuY8pZikc\n" +
      "Y9YmjtfAhX+rpB45ixEesqP2j5/X+VKFi30lQVeMQPUBZyamzppgOEDX3E1okTze\n" +
      "pYKCFl1eY6oZId5ekoe/aX8f/cDxbo2Q3JK55Syj189KX6DopzvWiaEl\n" +
      "-----END CERTIFICATE-----\n";

  private static final String CERT_2 =
      "-----BEGIN CERTIFICATE-----\n" +
      "MIIEkjCCA3qgAwIBAgIQCgFBQgAAAVOFc2oLheynCDANBgkqhkiG9w0BAQsFADA/\n" +
      "MSQwIgYDVQQKExtEaWdpdGFsIFNpZ25hdHVyZSBUcnVzdCBDby4xFzAVBgNVBAMT\n" +
      "DkRTVCBSb290IENBIFgzMB4XDTE2MDMxNzE2NDA0NloXDTIxMDMxNzE2NDA0Nlow\n" +
      "SjELMAkGA1UEBhMCVVMxFjAUBgNVBAoTDUxldCdzIEVuY3J5cHQxIzAhBgNVBAMT\n" +
      "GkxldCdzIEVuY3J5cHQgQXV0aG9yaXR5IFgzMIIBIjANBgkqhkiG9w0BAQEFAAOC\n" +
      "AQ8AMIIBCgKCAQEAnNMM8FrlLke3cl03g7NoYzDq1zUmGSXhvb418XCSL7e4S0EF\n" +
      "q6meNQhY7LEqxGiHC6PjdeTm86dicbp5gWAf15Gan/PQeGdxyGkOlZHP/uaZ6WA8\n" +
      "SMx+yk13EiSdRxta67nsHjcAHJyse6cF6s5K671B5TaYucv9bTyWaN8jKkKQDIZ0\n" +
      "Z8h/pZq4UmEUEz9l6YKHy9v6Dlb2honzhT+Xhq+w3Brvaw2VFn3EK6BlspkENnWA\n" +
      "a6xK8xuQSXgvopZPKiAlKQTGdMDQMc2PMTiVFrqoM7hD8bEfwzB/onkxEz0tNvjj\n" +
      "/PIzark5McWvxI0NHWQWM6r6hCm21AvA2H3DkwIDAQABo4IBfTCCAXkwEgYDVR0T\n" +
      "AQH/BAgwBgEB/wIBADAOBgNVHQ8BAf8EBAMCAYYwfwYIKwYBBQUHAQEEczBxMDIG\n" +
      "CCsGAQUFBzABhiZodHRwOi8vaXNyZy50cnVzdGlkLm9jc3AuaWRlbnRydXN0LmNv\n" +
      "bTA7BggrBgEFBQcwAoYvaHR0cDovL2FwcHMuaWRlbnRydXN0LmNvbS9yb290cy9k\n" +
      "c3Ryb290Y2F4My5wN2MwHwYDVR0jBBgwFoAUxKexpHsscfrb4UuQdf/EFWCFiRAw\n" +
      "VAYDVR0gBE0wSzAIBgZngQwBAgEwPwYLKwYBBAGC3xMBAQEwMDAuBggrBgEFBQcC\n" +
      "ARYiaHR0cDovL2Nwcy5yb290LXgxLmxldHNlbmNyeXB0Lm9yZzA8BgNVHR8ENTAz\n" +
      "MDGgL6AthitodHRwOi8vY3JsLmlkZW50cnVzdC5jb20vRFNUUk9PVENBWDNDUkwu\n" +
      "Y3JsMB0GA1UdDgQWBBSoSmpjBH3duubRObemRWXv86jsoTANBgkqhkiG9w0BAQsF\n" +
      "AAOCAQEA3TPXEfNjWDjdGBX7CVW+dla5cEilaUcne8IkCJLxWh9KEik3JHRRHGJo\n" +
      "uM2VcGfl96S8TihRzZvoroed6ti6WqEBmtzw3Wodatg+VyOeph4EYpr/1wXKtx8/\n" +
      "wApIvJSwtmVi4MFU5aMqrSDE6ea73Mj2tcMyo5jMd6jmeWUHK8so/joWUoHOUgwu\n" +
      "X4Po1QYz+3dszkDqMp4fklxBwXRsW10KXzPMTZ+sOPAveyxindmjkW8lGy+QsRlG\n" +
      "PfZ+G6Z6h7mjem0Y+iWlkYcV4PIWL1iwBi8saCbGS5jN2p8M+X+Q7UNKEkROb3N6\n" +
      "KOqkqm57TH2H3eDJAkSnh6/DNFu0Qg==\n" +
      "-----END CERTIFICATE-----\n";

}
