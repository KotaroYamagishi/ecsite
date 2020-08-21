$(function(){
    // 商品の値段
    var totalPrice=$(".sizeM-price").text();
    $("#total-price").text(totalPrice);

    // サイズが変更された時
    $(".size-box,.topping-checkbox,.quantity-selectbox").on("change", function(){
        var size=$(".size-box:checked").val();
        var topping_quantity=$(".topping-checkbox:checked").length;
        var toppingPrice=0;
        var quantity=$(".quantity-selectbox").val();
        var price=0;
        if(size=="M"){
            price=$(".sizeM-price").text();
            toppingPrice=200*topping_quantity;
        }else if(size=="L"){
            price=$(".sizeL-price").text();
            toppingPrice=300*topping_quantity;
        }
        var totalPrice=(price*quantity)+toppingPrice;
        $("#total-price").text(totalPrice);
    });
})

