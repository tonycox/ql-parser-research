package org.tonycox.ql;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Anton Solovev
 * @since 23.05.17.
 */
@Data
@Accessors(chain = true)
public class User {

    private String name;

    private String phone;
}
