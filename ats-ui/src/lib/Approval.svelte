<script lang="js">
  import { Label } from "@smui/data-table";
  import Card, { Actions } from "@smui/card";
  import Button from "@smui/button";
  let items = [];
  let sort = "id";
  let sortDirection = "ascending";

  if (typeof fetch !== "undefined") {
    fetch(
      "https://gist.githubusercontent.com/hperrin/e24a4ebd9afdf2a8c283338ae5160a62/raw/dcbf8e6382db49b0dcab70b22f56b1cc444f26d4/users.json"
    )
      .then((response) => response.json())
      .then((json) => (items = json));
  }

  function handleSort() {
    items.sort((a, b) => {
      const [aVal, bVal] = [a[sort], b[sort]][
        sortDirection === "ascending" ? "slice" : "reverse"
      ]();
      if (typeof aVal === "string" && typeof bVal === "string") {
        return aVal.localeCompare(bVal);
      }
      return Number(aVal) - Number(bVal);
    });
    items = items;
  }
</script>

<div>
  <div class="card-container">
    <Card>
      <Actions fullBleed>
        <Button on:click={() => console.log("Hai")}>
          <Label>Action</Label>
          <i class="material-icons" aria-hidden="true">arrow_forward</i>
        </Button>
      </Actions>
    </Card>
  </div>
  <div style="padding: 0;" >
      
  </div>
</div>
