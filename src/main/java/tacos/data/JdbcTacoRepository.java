package tacos.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import tacos.Ingredient;
import tacos.Taco;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

@Repository
public class JdbcTacoRepository implements TacoRepository {

    private JdbcTemplate jdbc;

    public JdbcTacoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Taco save(Taco design) {
        long tacoId = saveTacoInfo(design);

        design.setId(tacoId); //后续还能被识别吗？ 能因为save方法返回的是Taco，所以可以返回一个更完整的Taco

        for(Ingredient ingredient : design.getIngredients()) {
            saveIngredientToTaco(tacoId, ingredient);
        }
        return design;
    }
    private long saveTacoInfo(Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
                "insert into Taco(name, createdAt) values (?,?);",
                Types.VARCHAR, Types.TIMESTAMP).newPreparedStatementCreator(
                        Arrays.asList(taco.getName(), new Timestamp(taco.getCreatedAt().getTime())));

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(psc, keyHolder);

        return keyHolder.getKey().longValue();
    }
    private void saveIngredientToTaco(long id, Ingredient ingredient) {
        jdbc.update("insert into Taco_Ingredients(taco, ingredient) values(?, ?);", id, ingredient.getId());
    }
}
